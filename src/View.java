import javax.swing.*;
import java.awt.*;
import javax.imageio.*;
import java.io.*;
class View extends JPanel
{
    private Image sprite;
    private int[][] positions;
    View(int s) {
        JFrame frame = new JFrame("Galaxy");
        frame.add(this);
        frame.setSize(2 * s, s);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setFocusable(true);
        positions = new int[10][3];
        try
        {
            sprite = ImageIO.read(new File("resources/Sprite1.png"));
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    public void load(int[][] arr)
    {
        positions = arr;
    }
    public void render(int[][] data)
    {
        this.repaint();
    }

    public void paint(Graphics g)
    {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0,0,1000,500);
        if(positions!=null)
        {
            for (int i = 0; i < positions[0].length; i++)
            {
                g.drawImage(sprite, positions[0][i], positions[1][i],this);
            }
        }

    }

}