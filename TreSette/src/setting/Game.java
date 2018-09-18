package setting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import AI.Player;
import AI.PlayerAI;

public class Game
{
	private List<Card>[] assCarte = new LinkedList[4];
	private Player[] players = new PlayerAI[4];
	private Set<Card> carteInGioco = new HashSet<>();
	private int[] punteggi = {0, 0};
	private List<Card.Suit>[] semiAttivi = new LinkedList[4]; // player x semi

	public Game() {
		initialise();
	}

	/**
	 * esegue il gioco 
	 */
//	public void run()
//	{
//		
//		System.out.println("Inizio mano.");
//		System.out.println("Inizia il player "+assCarte[9]+".");
//		
//		int turno=assCarte[9];
//		
//		//10 "passate"
//		for(int g=1;g<=10;g++)
//		{
//			System.out.println(">Passata "+g+".");
//			
//			for(int j=0;j<4;j++)
//			{
//				PlayerAI p= players[(turno+j) % 4];
//				p.getMossa();
//			}
//		}
//		
//	}
//	
	
	
	public Set<Card> getExCards() {
		return new HashSet<>(carteInGioco);
	}

	public List<Card.Suit>[] getSemiAttivi() {
		List<Card.Suit>[] temp = new LinkedList[4];
		for(int i=0; i<4; i++)
			Collections.copy(temp[i], semiAttivi[i]);
		return temp;
	}

	/**
	 * Distribuisce le carte e inizializza i player
	 */
	private void initialise()
	{
		List<Integer> temp = new LinkedList<>();
		for(int i=0; i<40; i++)
			temp.add(i);
		
		Collections.shuffle(temp);
		
		for(int i=0; i<4; i++)
		{
			List<Card> carteInMano = new LinkedList<>(); 
			
			for(int j= 0; j<10; j++)
				carteInMano.add(new Card(temp.remove(0)));
			
			assCarte[i] = carteInMano;
			semiAttivi[i] = Arrays.asList(Card.Suit.values());
			
			players[i] = new PlayerAI(i, carteInMano, this);
		}
	}
}