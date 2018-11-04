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
    private Image sprite4;
    private Image sprite5;
    private Image sprite6;
    private Image sprite7;
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
            sprite4 = ImageIO.read(new File("resources/Sprite4.png"));
            sprite5 = ImageIO.read(new File("resources/Sprite5.png"));
            sprite6 = ImageIO.read(new File("resources/Sprite6.png"));
            sprite7 = ImageIO.read(new File("resources/Sprite6.png"));
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }
    public void dowin() {System.out.println("Good Job");}
    public void dolose() {System.out.println("Gane Over");}
    public void load(int[][]...arr)
    {
        if(arr[0]==null) return;
        int sum=0;
        for(int i=0; i<arr.length; i++)
        {
            if(arr[i]!=null) sum+=arr[i].length;
        }
        positions=new int[sum][4];
        sum=0;
        for(int i=0; i<arr.length; i++)
        {
            if(arr[i]!=null)
            {
                System.arraycopy(arr[i], 0, positions, sum, arr[i].length);
                if(arr[i]!=null) sum+=arr[i].length;
            }
        }
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
                switch (positions[i][3])
                {
                    case 1:
                        g.drawImage(sprite1, positions[i][0] - positions[i][2], positions[i][1] - positions[i][2], this);
                        break;
                    case 2:
                        g.drawImage(sprite2, positions[i][0] - positions[i][2], positions[i][1] - positions[i][2], this);
                        break;
                    case 3:
                        g.drawImage(sprite3, positions[i][0] - positions[i][2], positions[i][1] - positions[i][2], this);
                        break;
                    case 4:
                        g.drawImage(sprite4, positions[i][0] - positions[i][2], positions[i][1] - positions[i][2], this);
                        break;
                    case 5:
                        g.drawImage(sprite5, positions[i][0] - positions[i][2], positions[i][1] - positions[i][2], this);
                        break;
                    case 6:
                        g.drawImage(sprite6, positions[i][0] - positions[i][2], positions[i][1] - positions[i][2], this);
                        break;
                    case -6:
                        g.drawImage(sprite6, positions[i][0] - positions[i][2], positions[i][1] - positions[i][2], this);
                        g.setColor(Color.RED);
                        g.fillOval(positions[i][0] - positions[i][2] / 2, positions[i][1], positions[i][2], positions[i][2]);
                        break;
                    case -66:
                        g.drawImage(sprite6, positions[i][0] - positions[i][2], positions[i][1] - positions[i][2], this);
                        g.setColor(Color.RED);
                        g.fillOval(positions[i][0] - positions[i][2] / 2, positions[i][1], positions[i][2], positions[i][2]);
                        g.fillRect(positions[i][0] - positions[i][2] / 2, positions[i][1] + positions[i][2] / 2, positions[i][2], Controller.SIZE - positions[i][1] - positions[i][2]);
                        break;
                    case 7:
                        g.drawImage(sprite7, positions[i][0] - positions[i][2], positions[i][1] - positions[i][2], this);
                        break;
                    case 100:
                        g.drawImage(sprite0, positions[i][0] - positions[i][2], positions[i][1] - positions[i][2], this);
                        break;
                    case -1:
                        g2d.setColor(Color.MAGENTA);
                        g.fillOval(positions[i][0], positions[i][1], 5, 5);
                        break;
                    case -2:
                        g2d.setColor(Color.BLACK);
                        g.fillOval(positions[i][0], positions[i][1], 5, 5);
                        break;
                    default:
                        break;
                }
            }
        }

    }

}