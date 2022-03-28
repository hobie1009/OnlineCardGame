package Practice;

import javax.swing.*;
public class GUITest
{
    GUITest()
    {
        JFrame f = new JFrame("Add an image to JFrame");
        //ImageIcon icon = new ImageIcon("C:\\Users\\legio\\OneDrive\\Documents\\GitHub\\OnlineCardGame\\src\\Practice\\test.png");
        ImageIcon icon = new ImageIcon("C:\\Users\\legio\\OneDrive\\Desktop\\ORK.png");
        f.add(new JLabel(icon));
        f.pack();
        f.setVisible(true);
    }
    public static void main(String args[])
    {
        new GUITest();
    }
}