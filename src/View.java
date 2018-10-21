import javax.swing.*;
import java.awt.*;

class View extends JPanel
{
    private int[][] positions;
    View(int s)
    {
        JFrame frame = new JFrame("Galaxy");
        frame.add(this);
        frame.setSize(2*s, s);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setFocusable(true);
        positions=new int[10][3];
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
        g2d.setColor(Color.MAGENTA);
        if(positions!=null)
        {
            for (int i = 0; i < positions[0].length; i++) {
                g2d.fillOval(positions[0][i], positions[1][i], positions[2][i] * 2, positions[2][i] * 2);
            }
        }

    }
}