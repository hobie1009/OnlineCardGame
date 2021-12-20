package OnlineCardGame;

public class Card {
private	String imageURL;
private	int value;
private String valueAsString;
	public Card(String img, String v) {
		valueAsString = v;
		this.imageURL = img;
		char c = v.charAt(0);
		if (Character.isDigit(c)) {
			value = Integer.parseInt("" + c);
			if (value == 0) {
				value = 10;
			}
		} else {
			if (c == 'A') {
				value = 1;
			} else {
				value = 10;
			}
		}
		System.out.println("value" + value);
	}
	public String getImageURL() {
		return imageURL;
	}
	public int getValue() {
		return value;
	}
	public String getValueAsString() {
		return valueAsString;
	}
}
