package AI;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import AI.DeterministicAI;

public class AIGameState
{

	private static Random rand;
	private final List<List<Integer>> cardsAssignment;
	private final List<Integer> cardsOnTable;
	public final int currentPlayer;
	public final boolean terminal;
	public final double scoreSoFar;
	public final double scoreMyTeam;
	public final double scoreOtherTeam;
	public final boolean maxNode;

	public AIGameState(List<List<Integer>> cardsAssignment, List<Integer> cardsOnTable, int currentPlayer,
			boolean maxNode, double score1, double score2)
	{
		this.cardsAssignment = cardsAssignment;
		this.cardsOnTable = cardsOnTable;
		this.currentPlayer = currentPlayer;
		terminal = cardsAssignment.get(currentPlayer).isEmpty();
		if (terminal)
		{
			if (maxNode)
				score1 += 1.0 / 3;
			else
				score2 += 1.0 / 3;
		}
		double score = score1 - score2;
		scoreMyTeam = score1;
		scoreOtherTeam = score2;
		scoreSoFar = score;
		this.maxNode = maxNode;
	}

	/**
	 * Genera i successori dell'attuale GameState, mappati da mossa a stato
	 * 
	 * @return
	 */
	public Map<Integer, AIGameState> generateSuccessors()
	{

		if (terminal)
			return null;

		/*
		 * In mosse mettiamo tutte le mosse legali
		 */
		List<Integer> mosse = cardsOnTable.isEmpty() ? new ArrayList<>(cardsAssignment.get(currentPlayer))
				: DeterministicAI.possibiliMosse(cardsAssignment.get(currentPlayer), cardsOnTable.get(0) / 10);

		/*
		 * 
		 */
		HashMap<Integer, AIGameState> mappa = new HashMap<>();

		for (Integer m : mosse)
		{
			AIGameState g = genSuccessor(m);
			mappa.put(m, g);

		}

		return mappa;
	}

	/**
	 * Genera i successori dell'attuale GameState, mappati da mossa a stato
	 * 
	 * @return
	 */
	public List<Integer> generateActions()
	{

		if (terminal)
			return null;

		/*
		 * In mosse mettiamo tutte le mosse legali
		 */
		return cardsOnTable.isEmpty() ? new ArrayList<>(cardsAssignment.get(currentPlayer))
				: DeterministicAI.possibiliMosse(cardsAssignment.get(currentPlayer), cardsOnTable.get(0) / 10);

	}

	public Integer genRandMossa()
	{
		if (rand == null)
			rand = new Random();

		List<Integer> mosse = cardsOnTable.isEmpty() ? new ArrayList<>(cardsAssignment.get(currentPlayer))
				: DeterministicAI.possibiliMosse(cardsAssignment.get(currentPlayer), cardsOnTable.get(0) / 10);

		int pos = rand.nextInt(mosse.size());

		return mosse.get(pos);
	}

	public AIGameState genSuccessor(Integer mossa)
	{
		return genSuccessor(mossa, false);
	}

	public AIGameState genSuccessor(Integer mossa, boolean print)
	{
		List<List<Integer>> newCardsAssignment = new ArrayList<>(cardsAssignment);
		List<Integer> newCardsOnTable = new ArrayList<>(cardsOnTable);
		int newCurrentPlayer = -1;
		double newScoreMyTeam = scoreMyTeam;
		double newScoreOtherTeam = scoreOtherTeam;

		{
			List<Integer> temp = new ArrayList<>(newCardsAssignment.get(currentPlayer));
			temp.remove(mossa);
			newCardsAssignment.set(currentPlayer, temp);
			newCardsOnTable.add(mossa);
		}
		/*
		 * Caso semplice: non e' finita la passata ne la mano. Si passa al prossimo
		 * player e lo score rimane inalterato
		 */
		if (newCardsOnTable.size() < 4)
		{
			newCurrentPlayer = (currentPlayer + 1) % 4;
			AIGameState newGS = new AIGameState(newCardsAssignment, newCardsOnTable, newCurrentPlayer, !maxNode,
					scoreMyTeam, scoreOtherTeam);
			return newGS;
		} else
		{
			/*
			 * Siamo a fine di una passata. Dobbiamo assegnare i punti e la dominanza
			 */

			assert newCardsOnTable.size() == 4 : newCardsOnTable.size();
			int startingPlayer = (currentPlayer + 1) % 4;
			int playerDominante = startingPlayer;
			int cartaDominante = newCardsOnTable.get(0);
			int semeDominante = cartaDominante / 10;
			double punteggio = DeterministicAI.puntiPerCarta[cartaDominante % 10];
			for (int p = 1; p < 4; p++)
			{
				int cartaTemp = newCardsOnTable.get(p);
				punteggio += DeterministicAI.puntiPerCarta[cartaTemp % 10];
				if (semeDominante == cartaTemp / 10 && DeterministicAI.dominioPerCarta[cartaDominante
						% 10] < DeterministicAI.dominioPerCarta[cartaTemp % 10])
				{
					if (print)
						System.out.println(
								"sto sostituendo " + ((cartaDominante % 10) + 1) + " con " + (cartaTemp % 10 + 1));
					playerDominante = (startingPlayer + p) % 4;
					cartaDominante = cartaTemp;

				}
			}

			boolean newMaxNode = (maxNode && playerDominante % 2 == currentPlayer % 2)
					|| (!maxNode && playerDominante % 2 != currentPlayer % 2);
			// assegno i punti
			if (newMaxNode)
				newScoreMyTeam += punteggio;
			else
				newScoreOtherTeam += punteggio;

			// assegno player
			newCurrentPlayer = playerDominante;
			// assegno il maximise (stessa squadra)
			// svuoto la lista di carte sul tavolo
			newCardsOnTable.clear();
			AIGameState newGS = new AIGameState(newCardsAssignment, newCardsOnTable, newCurrentPlayer, newMaxNode,
					newScoreMyTeam, newScoreOtherTeam);
			return newGS;

		}
	}

	public double evaluationFunction()
	{
		double puntitot = 1.0 / 3;
		int dom1 = 0;
		int dom2 = 0;
		ArrayList<Integer> cardsTeam1 = new ArrayList<>();
		ArrayList<Integer> cardsTeam2 = new ArrayList<>();
		ArrayList<Integer> cards = new ArrayList<>();

		for (int p = 0; p < 4; p++)
		{
			if (p % 2 == 0)
				for (Integer c : cardsAssignment.get(p))
				{
					puntitot += DeterministicAI.puntiPerCarta[c % 10];
					cardsTeam1.add(c);
					cards.add(c);
				}
			else
				for (Integer c : cardsAssignment.get(p))
				{
					puntitot += DeterministicAI.puntiPerCarta[c % 10];
					cardsTeam2.add(c);
					cards.add(c);
				}
		}

		for (Integer c : cardsTeam1)
			for (Integer gc : cards)
				if (c / 10 == gc / 10
						&& DeterministicAI.dominioPerCarta[c % 10] > DeterministicAI.dominioPerCarta[gc % 10])
					dom1 += 1;

		for (Integer c : cardsTeam2)
			for (Integer gc : cards)
				if (c / 10 == gc / 10
						&& DeterministicAI.dominioPerCarta[c % 10] > DeterministicAI.dominioPerCarta[gc % 10])
					dom2 += 1;

		double lambda;

		if (maxNode ^ (currentPlayer % 2 == 0))
			lambda = dom2;
		else
			lambda = dom1;

		lambda /= dom1 + dom2;

		return (puntitot * lambda - puntitot * (1.0 - lambda)) + scoreSoFar;
	}

	/**
	 * @return the cardsAssignment
	 */
	public List<List<Integer>> getCardsAssignment()
	{
		return Collections.unmodifiableList(cardsAssignment);
	}

	/**
	 * @return the cardsOnTable
	 */
	public List<Integer> getCardsOnTable()
	{
		return Collections.unmodifiableList(cardsOnTable);
	}

	/**
	 * @return the currentPlayer
	 */
	public int getCurrentPlayer()
	{
		return currentPlayer;
	}

	public double getScoreSoFar()
	{
		return scoreMyTeam - scoreOtherTeam;
	}

	public double getScoreMyTeam()
	{
		return scoreMyTeam;
	}

	public double getScoreOtherTeam()
	{
		return scoreOtherTeam;
	}

	public boolean isCardsOnTableEmpty()
	{
		return cardsOnTable.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cardsAssignment == null) ? 0 : cardsAssignment.hashCode());
		result = prime * result + ((cardsOnTable == null) ? 0 : cardsOnTable.hashCode());
		result = prime * result + currentPlayer;
		result = prime * result + (maxNode ? 1231 : 1237);
		result = prime * result + (terminal ? 1231 : 1237);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AIGameState other = (AIGameState) obj;
		if (cardsAssignment == null)
		{
			if (other.cardsAssignment != null)
				return false;
		} else if (!cardsAssignment.equals(other.cardsAssignment))
			return false;
		if (cardsOnTable == null)
		{
			if (other.cardsOnTable != null)
				return false;
		} else if (!cardsOnTable.equals(other.cardsOnTable))
			return false;
		if (currentPlayer != other.currentPlayer)
			return false;
		if (maxNode != other.maxNode)
			return false;
		if (terminal != other.terminal)
			return false;
		return true;
	}

}
