import java.io.IOException;

class Controller
{
    private View view;
    private Model model;
    private GameLoop game;
    Controller(View view, Model model)
    {
        this.view=view;
        this.model=model;
        game=new GameLoop();
    }
    public void start()
    {
        game.run();
    }
    public static void main(String args[])
    {
        View view=new View(800);
        Model model=new Model(800);
        Controller controller=new Controller(view, model);
        controller.start();
        System.out.println("Galactic Attackers");
    }
    class GameLoop extends Thread
    {
        public void run()
        {
            try
            {
                while (true)
                {
                    model.movement(1);
                    view.load(model.getPositions());
                    view.repaint();
                    this.sleep(10);
                }
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }

        }
    }
}
