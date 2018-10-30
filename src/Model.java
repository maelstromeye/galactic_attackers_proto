import java.util.List;
import java.util.Vector;

class Model
{
    private boolean pause;
    private int difficulty;
    private int lives;
    private Ship ship;
    private int smallfries;
    private int runners;
    private int abnormals;
    private double[] seed;
    private List<Attacker> list;
    Model()
    {
        pause=false;
        list=new Vector();
        lives=3;
        difficulty=1;
        smallfries=0;
        runners=0;
        abnormals=0;
        Runner.weight=0.2;
        SmallFry.weight=0.1;
        ship=new Ship();
        seed=new double[10];
        seed[0]=randfrom(32,64);
        seed[1]=randfrom((double) seed[0]/2, (double) seed[0]);
        //seed[9]=randfrom((seed[0]>seed[9])?(double)seed[9]:(double)seed[0], (seed[0]>seed[9])?(double)seed[0]:(double)seed[9]);
        seed[2]=randfrom((double) seed[0]/8, (double) seed[0]/4);
        //seed[9]=randfrom((double)seed[9], (double)seed[0]);
        this.generator();
    }
    private int randfrom(double min, double max) {return (int) ((Math.random()*Math.abs(max-min))+((min<=max)?min:max));}
    public void generator() {
        int y, x;
        int[] quantity = new int[10];
        int rows = 0;
        smallfries = (int) (Math.sqrt(2*difficulty) / (SmallFry.weight + seed[1] / seed[0] * Runner.weight + seed[2] / seed[0] * Abnormal.weight));
        runners = (int) (seed[1] / seed[0] * smallfries);
        abnormals = (int) (seed[2] / seed[0] * smallfries);
        System.out.println(seed[0]);
        System.out.println(seed[1]);
        System.out.println(seed[2]);
        System.out.println(abnormals);
        System.out.println(runners);
        System.out.println(smallfries);
        rows=runners/10+1;
        for(int j=0;j<rows;j++) quantity[j]=runners/rows;
        for(int j=0;j<runners%rows;j++) quantity[j]++;
        for(int j=0; j<rows;j++)
        {
            x=Controller.SIZE-quantity[j]*Runner.radius;
            y=j*Runner.radius+25;
            for (int i = 0; i < quantity[j]; i++)
            {
                list.add(new Runner(x+i*Runner.radius*2, y, x-5*Runner.radius+i*Runner.radius*2, x+i*Runner.radius*2+5*Runner.radius));
            }
        }
        rows=smallfries/10+1;
        for(int j=0;j<rows;j++) quantity[j]=smallfries/rows;
        for(int j=0;j<smallfries%rows;j++) quantity[j]++;
        for(int j=0; j<rows;j++)
        {
            x=Controller.SIZE-quantity[j]*SmallFry.radius;
            y=j*SmallFry.radius+25+(runners/10+1)*Runner.radius;
            for (int i = 0; i < quantity[j]; i++)
            {
                list.add(new SmallFry(x+i*SmallFry.radius*2, y, x-5*SmallFry.radius+i*SmallFry.radius*2, x+i*SmallFry.radius*2+5*SmallFry.radius));
            }
        }
        if(abnormals>0)
        {
            rows=(int) ((double) abnormals/2+0.5);
            for (int j = 0; j < rows; j++) quantity[j] = abnormals / rows;
            for (int j = 0; j < abnormals % rows; j++) quantity[j]++;
            for (int j = 0; j < rows; j++)
            {
                x = Controller.SIZE - runners/(runners/10+1)*Runner.radius-10*Abnormal.radius;
                y = j*Abnormal.radius*2 + 25 + (runners / 10 + 1) * Runner.radius + (smallfries / 10 + 1) * SmallFry.radius;
                for (int i = 0; i < quantity[j]; i++)
                {
                    list.add(new Abnormal(x+i*(Controller.SIZE-x)*2, y, x+i*(Controller.SIZE-x)*2+((i==0)?(-5*Abnormal.radius):(-30*Abnormal.radius)), x+i*(Controller.SIZE-x)*2+((i==0)?30*Abnormal.radius:5*Abnormal.radius), (i==0)?true:false));
                }
            }
        }
    }
    /*private <T extends Attacker> void fillout(int number, T t, int index)
    {
        int[] quantity=new int[5];
        int y, x, rows=number/10+1;
        for(int j=0;j<rows;j++) quantity[j]=number/rows;
        for(int j=0;j<number%rows;j++) quantity[j]++;
        for(int j=0; j<rows;j++)
        {
            x=Controller.SIZE-quantity[j]*T.radius;
            y=j*T.radius+25;
            for (int i = 0; i < quantity[j]; i++)
            {
                list.add(index, new Abnormal(x+i*T.radius*2, y, x-5*T.radius+i*T.radius*2, x+i*T.radius*2+5*T.radius));
                index++;
            }
        }
    }*/
    public int[][] getPositions()
    {
        int[][] data=new int[list.size()+((ship==null)?0:1)][4];
        for(int i=0; i<list.size(); i++)
        {
            data[i][0]=list.get(i).getx();
            data[i][1]=list.get(i).gety();
            data[i][2]=list.get(i).radius;
            data[i][3]=list.get(i).getspr();
        }
        if(ship==null) return data;
        data[list.size()][0]=ship.getx();
        data[list.size()][1]=(Controller.SIZE-3*ship.getr());
        data[list.size()][2]=ship.getr();
        data[list.size()][3]=100;
        return data;
    }
    public int[][] getRockets()
    {
        int i, k=0;
        i=sumrock();
        int[][] data=new int[i][4];
        for(int j=0; j<list.size(); j++)
        {
            if(list.get(j).extract(0)!=null)
            {
                data[k]=list.get(j).extract(0);
                k++;
            }
        }
        return data;
    }
    public int[][] getMissiles()
    {
        if(ship==null) return null;
        if(ship.gets()==0) return null;
        int[][] data = new int[ship.gets()][4];
        for (int j=0; j<ship.gets(); j++)
        {
               data[j] = ship.extract(j);
        }
        return data;
    }
    public void movement(int i)
    {
        if(pause==true) return;
        for(int j=0;j<list.size();j++) list.get(j).movement(i);
        if(Math.random()>0.95)
        {
            for(Attacker a:list)
            {
                a.shoot();
            }
        }
        if(ship!=null) ship.fire(1);
    }
    public boolean checkwin()
    {
        if(list.isEmpty()==true)
        {
            difficulty+=5;
            ship.reload();
            this.generator();
            return true;
        }
        else return false;
    }
    public void skip()
    {
        list.clear();
        difficulty+=5;
        smallfries=0;
        runners=0;
        abnormals=0;
        ship.reload();
        this.generator();
    }
    public void hit()
    {
        if(pause==true) return;
        int[][] rockets=getMissiles();
        if (rockets!=null)
        {
            for (int i = 0; i < list.size(); i++)
            {
                if (rockets == null) break;
                for (int j = 0; j < rockets.length; j++)
                {
                    if (Math.sqrt(((list.get(i).getx() - rockets[j][0]) * (list.get(i).getx() - rockets[j][0]) + (list.get(i).gety() - rockets[j][1]) * (list.get(i).gety() - rockets[j][1]))) <= list.get(i).getr())
                    {
                        list.get(i).hit();
                        if (list.get(i).isgone() == true)
                        {
                            list.remove(i);
                            if (i < smallfries) smallfries--;
                            else if (i < runners + smallfries) runners--;
                            else abnormals--;
                        }
                        ship.shotdown(j);
                        rockets = getMissiles();
                        break;
                    }
                }
            }
        }
        int i=sumrock();
        System.out.println(i);
        if(i<=0) return;
        rockets=getRockets();
        if(ship!=null)
        {
            for (int j = 0; j < i; j++)
            {
                if (Math.sqrt(((ship.getx() - rockets[j][0]) * (ship.getx() - rockets[j][0]) + (Controller.SIZE - 3 * ship.getr() - rockets[j][1]) * (Controller.SIZE - 3 * ship.getr() - rockets[j][1]))) <= ship.getr())
                {
                    lives--;
                    pause = true;
                    ship = null;
                    for (Attacker a : list) a.reload();
                    break;
                }
            }
        }
        return;
    }
    public void pause() {pause=!pause; if(ship==null) ship=new Ship();}
    public void moveship(int j, int i) {if(pause==true) return; else if(ship!=null) ship.movement(j,i);}
    public void shoot() {if(pause==true) pause(); else if(ship!=null) ship.shoot();}
    public int sumrock() {int i=0; for(Attacker a:list) i+=a.gets(); return i;}
    class Ship
    {
        private int radius;
        private int crdx;
        private Missile[] rockets;
        private int shot;
        private int velocity;
        Ship()
        {
            crdx=Controller.SIZE;
            radius=25;
            shot=0;
            velocity=0;
            rockets=new Missile[3];
        }
        public int getx() {return crdx;}
        public int getr() {return radius;}
        public int gets() {return  shot;}
        public void reload() {rockets=new Missile[3]; shot=0;}
        public int[] extract(int k)
        {
            if((shot==0)||(k>=shot)) return null;
            int[] data=new int[4];
            data[0]=rockets[k].getx();
            data[1]=rockets[k].gety();
            data[2]=1;
            data[3]=-1;
            return data;
        }
        public void shotdown(int i)
        {
            if(i>=3) return;
            int j;
            for(j=i;j+1<rockets.length;j++)
            {
                rockets[j]=rockets[j+1];
            }
            rockets[j]=null;
            Missile[] data=rockets;
            rockets=new Missile[3];
            for(j=0;j<3;j++) rockets[j]=data[j];
            shot--;
        }
        public void movement(int j, int i)
        {
            if (j==-1) velocity=-8;
            else if (j==1) velocity=8;
            else if (j==2) shot++;
            else if (j==0) velocity=0;
            if((velocity==-1)&&(crdx<=radius)) return;
            else if((velocity==1)&&(crdx>=Controller.SIZE)) return;
            else crdx+=velocity*i;
        }
        public void fire(int i)
        {
            if(shot!=0)
            {
                for (int k = 0; k < shot; k++) {rockets[k].movement(i); }
                if (rockets[0].gety() <= 0)
                {
                    if(rockets[1]!=null)
                    {
                        if(rockets[2]!=null)
                        {
                            rockets[0] = rockets[1];
                            rockets[1] = rockets[2];
                            rockets[2] = null;
                        }
                        else
                        {
                            rockets[0]=rockets[1];
                            rockets[1]=null;
                        }
                    }
                    else rockets[0]=null;
                    shot--;
                }
            }
        }
        public void shoot()
        {
            if(shot==3) return;
            rockets[shot]=new Missile(crdx,Controller.SIZE-3*radius+1, false);
            shot++;
        }
    }
    static class Missile
    {
        private int crdx;
        private int crdy;
        private boolean hostile;
        private int velocity;
        private int counter;
        Missile(int x, int y, boolean b)
        {
            crdx=x;
            crdy=y;
            hostile=b;
            velocity=5;
            counter=0;
        }
        public void movement(int i)
        {
            if(hostile==true) crdy+=velocity*i;
            else crdy-=velocity*i;
        }
        public void vertmovement(int i, boolean b)
        {
            counter+=i;
            if(counter==2)
            {
                if(hostile==true) crdy+=velocity*i;
                else crdy-=velocity*i;
                counter=0;
                if(b==true) crdx+=i*velocity;
                else crdx-=i*velocity;
            }
        }
        public int getx() {return crdx;}
        public int gety() {return crdy;}
    }
    abstract static class Attacker
    {
        protected Missile[] rockets;
        protected int spritenum;
        protected int crdx;
        protected int crdy;
        public static int radius;
        protected int health;
        protected double velocity;
        protected boolean direction;
        protected int limitr;
        protected int limitl;
        protected int shot;
        Attacker() {}
        public int getx() {return crdx;}
        public int gety() {return crdy;}
        public int getr() {return radius;}
        public int getlr() {return limitr;}
        public int getll() {return limitl;}
        public int gets() {return shot;}
        public int getspr() {return spritenum;}
        public void setx(int x) {crdx=x;}
        public void sety(int y) {crdy=y;}
        public void setlr(int l) {limitr=l;}
        public void setll(int l) {limitl=l;}
        public void hit() {health--;}
        public abstract void reload();
        public boolean isgone()
        {
            if(health>0) return false;
            return true;
        }
        public void movement(int i)
        {
            if(direction==true) crdx+=i*velocity;
            else crdx-=i*velocity;
            if ((crdx-radius<=limitl)||(crdx+radius>=limitr)) direction=(!direction);
            if(shot==0) return;
            for(Missile m:rockets)
            {
                m.movement(i);
                if((m.gety()>=Controller.SIZE)&&(shot>=rockets.length))
                {
                    reload();
                    return;
                }
            }
        }
        public void shoot()
        {
            if(shot>=rockets.length) return;
            rockets[shot]=new Missile(crdx, crdy, true);
            shot++;
        }
        public int[] extract(int k)
        {
            if((shot==0)||(k>=shot)) return null;
            int[] data=new int[4];
            data[0]=rockets[k].getx();
            data[1]=rockets[k].gety();
            data[2]=1;
            data[3]=-2;
            return data;
        }
    }
    static class SmallFry extends Attacker
    {
        public static double weight;
        static
        {
            radius=25;
            weight=0.1;
        }
        SmallFry(int x, int y, int ll, int lr)
        {
            shot=0;
            rockets=new Missile[1];
            direction=true;
            spritenum=1;
            crdx=x;
            crdy=y;
            health=1;
            velocity=1;
            limitr=lr;
            limitl=ll;
        }
        public void movement(int i)
        {
            if(direction==true) crdx+=i*velocity;
            else crdx-=i*velocity;
            if ((crdx-radius<=limitl)||(crdx+radius>=limitr))
            {
                direction=(!direction);
                crdy+=50;
            }
            if(shot==0) return;
            for(Missile m:rockets)
            {
                m.movement(i);
                if((m.gety()>=Controller.SIZE)&&(shot>=rockets.length))
                {
                    reload();
                    return;
                }
            }
        }
        public void reload() {shot=0; rockets=new Missile[1];}
    }
    static class Runner extends Attacker
    {
        public static double weight;
        static
        {
            radius=20;
            weight=0.2;
        }
        Runner(int x, int y, int ll, int lr)
        {
            shot=0;
            rockets=new Missile[1];
            direction=true;
            spritenum=2;
            crdx=x;
            crdy=y;
            health=1;
            velocity=2;
            limitr=lr;
            limitl=ll;
        }
        public void movement(int i)
        {
            if(direction==true) crdx+=i*velocity;
            else crdx-=i*velocity;
            if ((crdx-radius<=limitl)||(crdx+radius>=limitr))
            {
                direction=(!direction);
            }
            if(shot==0) return;
            for(Missile m:rockets)
            {
                m.movement(i);
                System.out.println(shot);
                if((m.gety()>=Controller.SIZE)&&(shot>=rockets.length))
                {
                    reload();
                    return;
                }
            }
        }
        public void reload() {shot=0; rockets=new Missile[1];}
    }
    static class Abnormal extends Attacker
    {
        public static double weight;
        private int counter;
        static
        {
            radius=25;
            weight=0.5;
        }
        Abnormal(int x, int y, int ll, int lr, boolean b)
        {
            shot=0;
            rockets=new Missile[1];
            direction=true;
            spritenum = 3;
            crdx = x;
            crdy = y;
            health = 2;
            velocity = 1;
            limitr = lr;
            limitl = ll;
            direction=b;
        }
        public void movement(int i)
        {
            counter+=i;
            if(counter==10)
            {
                crdy++;
                counter=0;
            }
            if (direction == true) crdx+=i*velocity;
            else crdx-=i*velocity;
            if ((crdx - radius <= limitl) || (crdx + radius >= limitr))
            {
                direction = (!direction);
                crdy+=radius*3;
            }
            if(shot==0) return;
            for(Missile m:rockets)
            {
                m.vertmovement(i, direction);
                if((m.gety()>=Controller.SIZE)&&(shot>=rockets.length))
                {
                    reload();
                    return;
                }
            }
        }
        public void reload() {shot=0; rockets=new Missile[1];}
    }
}
