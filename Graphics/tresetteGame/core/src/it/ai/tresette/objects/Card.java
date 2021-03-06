package it.ai.tresette.objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Card extends Entity {

	private String path = "";
	
	
	private Texture texture;
	
	private TextureRegion texRegion;
	
	private int textureWidth = Constants.TABLE_EDGE/10;
	
	private int textureHeight =(int) ((int) (Constants.TABLE_EDGE/10) * 1.5); //cosi facendo le proporzioni sono 6/9 tra larghezze e altezza
	
	private static final Suit[] intToSuit = {Suit.COPPE, Suit.DENARI, Suit.BASTONI, Suit.SPADE};
	private static final Val[] intToVal = {Val.ASSO, Val.DUE, Val.TRE, Val.QUATTRO, Val.CINQUE, Val.SEI, Val.SETTE, Val.FANTE, Val.CAVALLO, Val.RE};
	
	public enum Suit { 
		
		COPPE(0,"coppe"), DENARI(10,"oro"), BASTONI(20,"bastoni"), SPADE(30,"spade");
		
		private int val;
		
		private String pathString;
		
		private Suit(int value,String path) { this.val = value; this.pathString = path;}
		
		private Suit(int value) {this.val = value;}
		
		public int getVal() { return val; }
		
		@Override
		public String toString(){return pathString;	}
	}
	
	public enum Val 
	{ 
		//la logica e': stringa per il path della texture - numero della carta - potenza della carta - valore in punti della carta
		ASSO("uno",0,7,3), DUE("due",1,8,1), TRE("tre",2,9,1), QUATTRO("quattro",3,0,0), CINQUE("cinque",4,1,0), SEI("sei",5,2,0), SETTE("sette",6,3,0), FANTE("fante",7,4,1), CAVALLO("cavaliere",8,5,1), RE("re",9,6,1);
		
		/**
		 * the int representation of the card; where Asso is 0 and Re is 9
		 */
		private int cardNr;
		
		/**
		 * the dominance of each Card where Dominance(TRE) = 9 and Dominance(Quattro) = 0
		 */
		private int dominanza;
		
		/**
		 * the value in points of each Card where Points(ASSO) = 3 and Points(TRE) = 1 and point(CINQUE) = 0
		 */
		private int punteggio;
		
		/**
		 * The string used for constructing the path of the texture
		 */
		private String stringPath;
		
		private Val(String stringPath,int cardNr, int dominanza, int punteggio) {this.stringPath = stringPath; this.cardNr = cardNr; this.dominanza = dominanza; this.punteggio = punteggio; }
		
		private Val(int cardNr) { this.cardNr = cardNr;}
		
		public int getCardNr() {return this.cardNr;}
		
		public int getDominanza() {return this.dominanza;}
		
		public int getPoints() {return this.punteggio;}
		
		@Override
		public String toString(){return this.stringPath;}
		
		public int moreDominant(Val other)
		{
			return this.dominanza - other.getDominanza();
		}
	
	
	}
	
	private Suit suit;
	
	private Val val;
	
	
	/**
	 * Return the card represented by the number cardNr.
	 * 0-9 -> Coppe card from Asso(0) to Re(9)
	 * 10-19 -> Denari // //
	 * 20-29 -> Bastoni // // 
	 * 30-39 -> Spade // // 
	 * @param cardNr
	 */
	public Card(int cardNr)
	{
		if(cardNr < 0 || cardNr > 39) return;
		
		this.suit = intToSuit[cardNr/10];
		this.val = intToVal[cardNr%10];
		this.path = "cards/"+suit.toString() + "_" + val.toString() + ".png";
		this.texture = new Texture(this.path);
		texRegion = new TextureRegion(texture);
	}
	
	/**
	 * Return the card with Suit suit and Val val;
	 * @param suit the suit of the card
	 * @param val the val of the card
	 */
	public Card(Suit suit, Val val)
	{
		this.suit = suit;
		this.val = val;
		this.path = "cards/"+suit.toString() + "_" + val.toString() + ".png";
		this.texture = new Texture(this.path);
		texRegion = new TextureRegion(texture);
		
		
	}
	
	/**
	 * this method draws the card in the coordinates x,y
	 * @param batch
	 * @param x
	 */
	public void draw(SpriteBatch batch, int x, int y)
	{
		
		//batch.draw(texture, (float)x, (float)y, 0f, 0f, (float)textureWidth, (float)textureHeight, 1f, 1f);
		batch.draw(texRegion, x, y, 0, 0, textureWidth, textureHeight, 1, 1, 0);
	}
	/**
	 * this method draws the card in the coordinates x,y and with rotation rot
	 * @param batch
	 * @param x
	 */
	public void draw(SpriteBatch batch, int x ,int y,int rot)
	{
		
	}
	
	
	
	public int toInt()
	{
		return this.suit.getVal() + this.val.getCardNr();
	}
	
	/**
	 * returns the suit of the card represented by an int
	 * @return
	 */
	public int getIntSuit()
	{
		return this.suit.getVal();
	}
	
	public int getPoints() {return this.val.getPoints();}
	
	public int getCardnr() {return this.val.getCardNr()+this.suit.getVal();}
	
	@Override
	public boolean equals(Object other)
	{
		if(this == other) return true;
		if(!(other instanceof Card)) return false;
		Card temp = (Card)other;
		return(this.suit.equals(temp.suit) && this.val.equals(temp.val));
	}
	
	public int compareTo(Card dominatingCard) {
		if(this.suit.equals(dominatingCard.suit)) 
			return this.val.moreDominant(dominatingCard.val);
		
		return 0;
		
	}
	
	
	public String toString()
	{
		return this.val.toString()+" di "+this.suit.toString();
	}
	
	
	public static void main(String[] args) {
		//main for tests purpose
//		Card a = new Card(Suit.BASTONI, Val.TRE);
//		System.out.println(a.suit.getVal() + a.val.getCardNr() );
//		System.out.println("the path is "+a.suit.toString()+"_"+a.val.toString()+".png");
		Card b = new Card(12);
		System.out.println(b.suit.getVal() + b.val.getCardNr() );
		System.out.println("the path is "+b.suit.toString()+"_"+b.val.toString()+".png");
		System.out.println(b.val.getPoints());
		System.out.println(b.equals(b));
		System.out.println(b.path);
		// System.out.println(b.toString());
		
		 
	}

	
	
	
	
}
