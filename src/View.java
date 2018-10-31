import javax.swing.*;
import java.awt.*;
import javax.imageio.*;
import java.io.*;
class View extends JPanel
{
    private Image sprite0;
    private Image sprite1;
    private Image sprite2;
    private Image sprite3;
    private int[][] positions;
    View()
    {
        JFrame frame = new JFrame("Galaxy");
        frame.add(this);
        frame.setSize(2 * Controller.SIZE, Controller.SIZE);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setFocusable(true);
        try
        {
            sprite0 = ImageIO.read(new File("resources/Sprite0.png"));
            sprite1 = ImageIO.read(new File("resources/Sprite1.png"));
            sprite2 = ImageIO.read(new File("resources/Sprite2.png"));
            sprite3 = ImageIO.read(new File("resources/Sprite3.png"));
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    public void dowin() {System.out.println("Good Job");}
    public void load(int[][] arr)
    {
        if(arr==null) return;
        positions=new int[arr.length][4];
        for(int i=0;i<arr.length;i++)
        {
            positions[i][0]=arr[i][0];
            positions[i][1]=arr[i][1];
            positions[i][2]=arr[i][2];
            positions[i][3]=arr[i][3];
        }
    }
    public void append(int[][] arr)
    {
        if(arr==null) return;
        int[][] data=positions;
        positions=new int[data.length+arr.length][4];
        for(int i=0;i<data.length;i++)
        {
            positions[i]=data[i];
        }
        for(int i=0;i<arr.length;i++)
        {
            positions[i+data.length]=arr[i];
        }
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
        g2d.fillRect(0,0,Controller.SIZE*2,Controller.SIZE);
        if(positions!=null)
        {
            for (int i = 0; i < positions.length; i++)
            {
                if(positions[i][3]==1)
                {
                    g.drawImage(sprite1, positions[i][0]-positions[i][2], positions[i][1]-positions[i][2],this);
                }
                else if(positions[i][3]==2)
                {
                    g.drawImage(sprite2, positions[i][0]-positions[i][2], positions[i][1]-positions[i][2],this);
                }
                else if(positions[i][3]==3)
                {
                    g.drawImage(sprite3, positions[i][0]-positions[i][2], positions[i][1]-positions[i][2],this);
                }
                else if(positions[i][3]==100)
                {
                    g.drawImage(sprite0, positions[i][0]-positions[i][2]-1, positions[i][1],this);
                }
                else if(positions[i][3]==-1)
                {
                    g2d.setColor(Color.MAGENTA);
                    g.fillOval(positions[i][0], positions[i][1], 5,5);
                }
                else if(positions[i][3]==-2)
                {
                    g2d.setColor(Color.BLACK);
                    g.fillOval(positions[i][0], positions[i][1], 5,5);
                }
            }
        }

    }

}