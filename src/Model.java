
import java.util.List;
import java.util.Vector;

class Model
{
    private int difficulty;
    private int lives;
    private Ship ship;
    private int smallfries;
    private int runners;
    private static int[] seed;
    List<Attacker> list;
    Model()
    {
        list=new Vector();
        lives=3;
        difficulty=1;
        smallfries=0;
        runners=0;
        Runner.weight=0.2;
        SmallFry.weight=0.1;
        ship=new Ship();
        seed=new int[10];
        seed[0]=(int)(Math.random()*32)+32;
        seed[1]=(int)(Math.random()*seed[0])+seed[0]/2;
        seed[2]=(int)((Math.random()*Math.abs(seed[1]-seed[0]))+((seed[0]>seed[1])?seed[1]:seed[0]));
        this.generator();
    }
    public void generator()
    {
        double k=(double)seed[2]/(double)seed[0]*(difficulty%4+1)/2.5;
        runners= (int) (Math.sqrt(difficulty)/(Runner.weight+(k*SmallFry.weight)));
        smallfries= (int) (runners*k);
        System.out.println(k);
        System.out.println(seed[0]);
        System.out.println(seed[1]);
        System.out.println(seed[2]);
        System.out.println(runners);
        System.out.println(smallfries);
        for (int i=0; i<smallfries; i++)
        {
            list.add(new SmallFry());
            list.get(i).setx(25+i*50);
            list.get(i).setlr(Controller.SIZE-3*list.get(i).getr()+i*50);
            list.get(i).setll(i*50);
        }
        for (int i=0; i<runners; i++)
        {
            list.add(new Runner());
            list.get(i+smallfries).setx(25+i*50);
            list.get(i+smallfries).setlr(Controller.SIZE-3*list.get(i+smallfries).getr()+i*50);
            list.get(i+smallfries).setll(i*50);
        }
    }
    public int[][] getPositions()
    {
        int[][] data=new int[runners+smallfries+1][4];
        for(int i=0; i<smallfries+runners; i++)
        {
            data[i][0]=list.get(i).getx();
            data[i][1]=list.get(i).gety();
            data[i][2]=list.get(i).getr();
            data[i][3]=list.get(i).gets();
        }
        data[smallfries+runners][0]=ship.getx();
        data[smallfries+runners][1]=(Controller.SIZE-3*ship.getr());
        data[smallfries+runners][2]=ship.getr();
        data[smallfries+runners][3]=100;
        return data;
    }
    public int[][] getMissiles()
    {
        if(ship.gets()==0) return null;
        int[][] data=new int[ship.gets()][4];
        for(int i=0;i<ship.gets();i++)
        {
            data[i]=ship.extract(i);
        }
        return data;
    }
    public void movement(int i)
    {
        for(int j=0;j<smallfries+runners;j++) {list.get(j).movement(i);}
    }
    public boolean checkwin()
    {
        if(list.isEmpty()==true)
        {
            difficulty++;
            ship.reload();
            this.generator();
            return true;
        }
        else return false;
    }
    public int[][] hit()
    {
        int[][] rockets=getMissiles();
        if (rockets==null) return getPositions();
        double dist=0;
        int x, y, r;
        for (int i = 0; i<runners+smallfries; i++)
        {
            if(rockets[0]==null) break;
            x=list.get(i).getx();
            y=list.get(i).gety();
            r=list.get(i).getr();
            for(int j=0;j<rockets.length;j++)
            {
                dist=Math.sqrt(((x-rockets[j][0])*(x-rockets[j][0])+(y-rockets[j][1])*(y-rockets[j][1])));
                if (dist<=r)
                {
                    list.get(i).hit();
                    if(list.get(i).isgone()==true)
                    {
                        list.remove(i);
                        if (i>=smallfries) runners--;
                        else smallfries--;
                    }
                    ship.shotdown(j);
                    continue;
                }
            }
        }
        return getPositions();
    }
    public void fire() {ship.fire();}
    public void moveship(int j, int i) {ship.movement(j,i);}
    public void shoot() {ship.shoot();}
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
        public void fire()
        {
            if(shot!=0)
            {
                for (int k = 0; k < shot; k++) {rockets[k].movement(1); }
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
    class Missile
    {
        private int crdx;
        private int crdy;
        private boolean hostile;
        private int velocity;
        Missile(int x, int y, boolean b)
        {
            crdx=x;
            crdy=y;
            hostile=b;
            velocity=5;
        }
        public void movement(int i)
        {
            if(hostile==true) crdy+=velocity*i;
            else crdy-=velocity*i;
        }
        public int getx() {return crdx;}
        public int gety() {return crdy;}
    }
    abstract static class Attacker
    {
        protected int spritenum;
        protected int crdx;
        protected int crdy;
        protected int radius;
        protected int health;
        protected int velocity;
        protected boolean direction;
        protected int limitr;
        protected int limitl;
        Attacker()
        {
            spritenum=1;
            crdx=25;
            crdy=25;
            radius=25;
            health=1;
            velocity=1;
            limitr=100;
            limitl=0;
            direction=true;
        }
        public int getx() {return crdx;}
        public int gety() {return crdy;}
        public int getr() {return radius;}
        public int getlr() {return limitr;}
        public int getll() {return limitl;}
        //public double getw() {return weight;}
        public int gets() {return spritenum;}
        public void setx(int x) {crdx=x;}
        public void sety(int y) {crdy=y;}
        public void setlr(int l) {limitr=l;}
        public void setll(int l) {limitl=l;}
        public void hit() {health--;}
        public boolean isgone()
        {
            if(health>0) return false;
            return true;
        }
        public void movement(int m)
        {
            if(direction==true) crdx+=m*velocity;
            else crdx-=m*velocity;
            if ((crdx-radius<=limitl)||(crdx+radius>=limitr)) direction=(!direction);
        }
    }
    static class SmallFry extends Attacker
    {
        public static double weight;
        SmallFry()
        {
            spritenum=1;
            crdx=25;
            crdy=25;
            radius=25;
            health=1;
            weight=0.1;
            velocity=1;
            limitr=100;
            limitl=0;
        }
        public void movement(int m)
        {
            if(direction==true) crdx+=m*velocity;
            else crdx-=m*velocity;
            if ((crdx-radius<=limitl)||(crdx+radius>=limitr))
            {
                direction=(!direction);
                crdy+=50;
            }
        }
    }
    static class Runner extends Attacker
    {
        public static double weight;
        Runner()
        {
            spritenum=2;
            crdx=25;
            crdy=100;
            radius=25;
            health=1;
            weight=0.2;
            velocity=2;
            limitr=100;
            limitl=0;
        }
        public void movement(int m)
        {
            if(direction==true) crdx+=m*velocity;
            else crdx-=m*velocity;
            if ((crdx-radius<=limitl)||(crdx+radius>=limitr))
            {
                direction=(!direction);

            }
        }
    }
}
