import java.util.*;
import java.util.zip.CheckedOutputStream;

class Model
{
    private boolean pause;
    private int difficulty;
    private int lives;
    private int stage;
    private double score;
    private static Ship ship;
    private static double[] seed;
    private static Vector<Attacker> list;
    private double modifier;
    private Vector<Missile> freemiss;
    private static List<Attacker> queue;
    Model()
    {
        pause=false;
        list=new Vector<>();
        lives=3;
        score=0;
        stage=1;
        difficulty=50;
        ship=new Ship();
        seed=new double[11];
        seed[0]=randfrom(128,256);
        seed[1]=seed[0];
        seed[2]=randfrom(seed[0]/2, seed[0]*2);
        seed[3]=randfrom(seed[0]/6, seed[0]/3);
        seed[4]=randfrom(seed[0]/8, seed[0]/4);
        seed[5]=randfrom(seed[0]/16, seed[0]/8);
        seed[6]=randfrom(seed[0]/8, seed[0]/4);
        seed[7]=randfrom(seed[0]/12, seed[0]/4);
        seed[8]=randfrom(seed[0]/8, seed[0]/4);
        seed[9]=randfrom(seed[0]+1, 2*seed[0]+1);
        modifier=seed[0]*107/109;
        if(modifier>0.2) modifier=modifier/2;
        generator();
        freemiss=null;
        queue=new Vector<>();
    }
    private double randfrom(double min, double max) {return (Math.random()*Math.abs(max-min))+((min<=max)?min:max);}
    private double evolve() {seed[10]=(seed[10]*(seed[0]*947*109/107)+(seed[0]*947*109/107))%seed[0]+seed[0]/2; return seed[0]/seed[10];}
    public void generator()
    {
        int y, x, smallfries, runners, abnormals, thiccboyes, hivewitches, laserboys, lamps, glitches;
        double e;
        int[] quantity = new int[10];
        int rows;
        x=(int) (Math.sqrt(difficulty)/(SmallFry.weight*seed[1]/seed[0]+seed[2]/seed[0]*Runner.weight+seed[3]/seed[0]*Abnormal.weight+seed[4]/seed[0]*Thiccboy.weight+seed[5]/seed[0]*Hivewitch.weight+seed[6]/seed[0]*Laserboy.weight+seed[7]/seed[0]*Lamp.weight+seed[8]/seed[0]*Glitch.weight));
        do
        {
            smallfries= (int) (seed[1]/seed[0]*x*evolve());
            runners=(int) (seed[2]/seed[0]*x*evolve());
            abnormals = (int) (seed[3]/seed[0]*x*evolve());
            thiccboyes = (int) (seed[4]/seed[0]*x*evolve());
            hivewitches = (int) (seed[5]/seed[0]*x*evolve());
            laserboys = (int) (seed[6]/seed[0]*x*evolve());
            lamps = (int) (seed[7]/seed[0]*x*evolve());
            glitches=(int) (seed[8]/seed[0]*x*evolve());
            e=smallfries*SmallFry.weight+runners*Runner.weight+abnormals*Abnormal.weight+thiccboyes*Thiccboy.weight+hivewitches*Hivewitch.weight+laserboys*Laserboy.weight+lamps*Lamp.weight+glitches*Glitch.weight;
        }
        while((Math.sqrt(difficulty)/2>e)&&(e>Math.sqrt(difficulty)));
        if(laserboys>0)
        {
            rows=(int) ((double) laserboys/6+0.84);
            for (int j = 0; j < rows; j++) quantity[j] = laserboys / rows;
            for (int j = 0; j < laserboys % rows; j++) quantity[j]++;
            for (int j = 0; j < rows; j++)
            {
                x = Controller.SIZE-quantity[j]*Laserboy.radius+2*Laserboy.radius;
                y =  j*Laserboy.radius*2 + 25;
                for (int i = 0; i < quantity[j]; i++)
                {
                    list.add(new Laserboy(x+((2*(i%6)+(i%6+1-(i+1)%2)/2)%6-2)*Laserboy.radius*2, y, x+((2*(i%6)+(i%6+1-(i+1)%2)/2)%6-2)*Laserboy.radius*2+(((2*(i%6)+(i%6+1-(i+1)%2)/2)%6-2<=0)?(-10*Laserboy.radius):(-Laserboy.radius)), x+((2*(i%6)+(i%6+1-(i+1)%2)/2)%6-2)*Laserboy.radius*2+((((2*(i%6)+(i%6+1-(i+1)%2)/2)%6-2)<=0)?(Laserboy.radius):(10*Laserboy.radius)),(((2*(i%6)+(i%6+1-(i+1)%2)/2)%6-2)>0)));
                }
            }
        }
        if (runners>0)
        {
            rows=(int) ((double) runners/10+0.9);
            for(int j=0;j<rows;j++) quantity[j]=runners/rows;
            for(int j=0;j<runners%rows;j++) quantity[j]++;
            for(int j=0; j<rows;j++)
            {
                x=Controller.SIZE-quantity[j]*Runner.radius;
                y=(int)((double) laserboys/6+0.84)*Laserboy.radius*2 + j*Runner.radius*2+25;
                for (int i = 0; i < quantity[j]; i++)
                {
                    list.add(new Runner(x+i*Runner.radius*2, y, x-5*Runner.radius+i*Runner.radius*2, x+i*Runner.radius*2+5*Runner.radius));
                }
            }
        }
        if(smallfries>0)
        {
            rows=(int) ((double) smallfries/10+0.9);
            for(int j=0;j<rows;j++) quantity[j]=smallfries/rows;
            for(int j=0;j<smallfries%rows;j++) quantity[j]++;
            for(int j=0; j<rows;j++)
            {
                x=Controller.SIZE-quantity[j]*SmallFry.radius;
                y=j*SmallFry.radius*2+25+(int)((double) runners/10+0.9)*Runner.radius*2+(int)((double) laserboys/6+0.84)*Laserboy.radius*2;
                for (int i = 0; i < quantity[j]; i++)
                {
                    list.add(new SmallFry(x+i*SmallFry.radius*2, y, x-10*SmallFry.radius+i*SmallFry.radius*2, x+i*SmallFry.radius*2+10*SmallFry.radius));
                }
            }
        }
        if(abnormals>0)
        {
            rows=(int) ((double) abnormals/2+0.5);
            for (int j = 0; j < rows; j++) quantity[j] = abnormals / rows;
            for (int j = 0; j < abnormals % rows; j++) quantity[j]++;
            for (int j = 0; j < rows; j++)
            {
                x = (int) (Controller.SIZE - runners/((double) runners / 10 + 0.9)*Runner.radius-10*Abnormal.radius);
                y = j*Abnormal.radius*2 + 25 + (int)((double) runners / 10 + 0.9)*Runner.radius + (int)((double) smallfries / 10 + 0.9) * SmallFry.radius;
                for (int i = 0; i < quantity[j]; i++)
                {
                    list.add(new Abnormal(x+i*(Controller.SIZE-x)*2, y, x+i*(Controller.SIZE-x)*2+((i==0)?(-5*Abnormal.radius):(-30*Abnormal.radius)), x+i*(Controller.SIZE-x)*2+((i==0)?30*Abnormal.radius:5*Abnormal.radius), i==0));
                }
            }
        }
        if(hivewitches>0)
        {
            rows=(int) ((double) hivewitches/5+0.8);
            for(int j=0;j<rows;j++) quantity[j]=hivewitches/rows;
            for(int j=0;j<hivewitches%rows;j++) quantity[j]++;
            for(int j=0; j<rows;j++)
            {
                x=Controller.SIZE-quantity[j]*Hivewitch.radius;
                y=j*Hivewitch.radius*2+25+(int)((double) runners/10+0.9)*Runner.radius*2+(int)((double) smallfries/10+0.9)*SmallFry.radius*2+(int)((double) laserboys/6+0.84)*Laserboy.radius*2;
                for (int i = 0; i < quantity[j]; i++)
                {
                    list.add(new Hivewitch(x+i*Hivewitch.radius*2, y, x-10*Hivewitch.radius+i*Hivewitch.radius*2, x+i*Hivewitch.radius*2+10*Hivewitch.radius));
                }
            }
        }
        if(thiccboyes>0)
        {
            rows=(int) ((double) thiccboyes/5+0.8);
            for(int j=0;j<rows;j++) quantity[j]=thiccboyes/rows;
            for(int j=0;j<thiccboyes%rows;j++) quantity[j]++;
            for(int j=0; j<rows;j++)
            {
                x=Controller.SIZE-quantity[j]*Thiccboy.radius;
                y=j*Thiccboy.radius*2+25+(int)((double) runners/10+0.9)*Runner.radius*2+(int)((double) smallfries/10+0.9)*SmallFry.radius*2+(int)((double) hivewitches/5+0.8)*Hivewitch.radius*2+(int)((double) laserboys/6+0.84)*Laserboy.radius*2;
                for (int i = 0; i < quantity[j]; i++)
                {
                    list.add(new Thiccboy(x+i*Thiccboy.radius*2, y, x-10*Thiccboy.radius+i*Thiccboy.radius*2, x+i*Thiccboy.radius*2+10*Thiccboy.radius));
                }
            }
        }
        if(lamps>0) for (int i=0; i<lamps; i++) list.add(new Lamp(Controller.SIZE, 225, seed[0]/seed[10]>=1, (270+360/(double) lamps*i)%360*Math.PI/180));
        if(glitches>0) for(int i=0; i<glitches; i++) list.add(new Glitch((int) (evolve()*947*109/107)%(Controller.SIZE*2)+Glitch.radius, (int) (evolve()*947/107*109)%(Controller.SIZE*6/5-300)+Glitch.radius, evolve()));
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
    public void boss()
    {
        list.add(new Gargantua(Controller.SIZE, Gargantua.radius,500, seed[0]/seed[10]));
    }
    public int[][] getPositions()
    {
        if(pause) return null;
        int[][] data=new int[list.size()+((ship==null)?0:1)][4];
        for(int i=0; i<list.size(); i++)
        {
            data[i][0]=list.get(i).getx();
            data[i][1]=list.get(i).gety();
            data[i][2]=list.get(i).getr();
            data[i][3]=list.get(i).getspr();
        }
        if(ship==null) return data;
        data[list.size()][0]=ship.getx();
        data[list.size()][1]=Ship.HEIGHT;
        data[list.size()][2]=Ship.radius;
        data[list.size()][3]=100;
        return data;
    }
    public int[][] getRockets()
    {
        if(pause) return null;
        int k=0;
        Missile missile;
        int[][] data=new int[sumrock()][4];
        for(int i=0; i<list.size(); i++)
        {
            for (int j=0; j<list.get(i).gets(); j++)
            {
                if (list.get(i).extract(j)!=null)
                {
                    missile=list.get(i).extract(j);
                    data[k][0]=missile.getx();
                    data[k][1]=missile.gety();
                    data[k][2]=1;
                    if(missile.gety()==Ship.HEIGHT) data[k][3]=-5000;
                    else data[k][3]=-2;
                    k++;
                }
            }
        }
        return data;
    }
    public int[][] getMissiles()
    {
        if(pause) return null;
        if(ship==null) return null;
        if(ship.gets()==0) return null;
        int j=ship.gets();
        Missile missile;
        int[][] data = new int[j][4];
        for (int i=0; i<j; i++)
        {
            missile=ship.extract(i);
            data[i][0]=missile.getx();
            data[i][1]=missile.gety();
            data[i][2]=1;
            data[i][3]=-1;
        }
        return data;
    }
    public int[][] getfree()
    {
        if(pause) return null;
        if(freemiss==null) return null;
        int[][] data=new int[freemiss.size()][4];
        for(int i=0; i<freemiss.size(); i++)
        {
            data[i][0]=freemiss.get(i).getx();
            data[i][1]=freemiss.get(i).gety();
            data[i][2]=1;
            data[i][3]=-2;
        }
        return data;
    }
    public void movement(int i)
    {
        if(pause) return;
        if(i==0) return;
        for(Attacker a: list) a.movement(i);
        for(Attacker a:list)
        {
            modifier=((seed[0]*947*109/107)*modifier+seed[0]/seed[9]*107/109)%100;
            if(modifier+Math.log(Controller.SIZE-a.gety())>106.3) a.shoot();
        }
        list.addAll(queue);
        queue.clear();
        if(freemiss!=null)
        {
            for(int j=0; j<freemiss.size(); j++)
            {
                freemiss.get(j).movement(i);
                if(freemiss.get(j).gety()>=Ship.HEIGHT+Ship.radius)
                {
                    freemiss.remove(j);
                    j--;
                }
                if(freemiss.isEmpty())
                {
                    freemiss=null;
                    break;
                }
            }
        }
        if(ship!=null)
        {
            ship.movement(1,4);
            ship.shoot(2);
        }
    }
    public boolean checkwin()
    {
        if(list.isEmpty())
        {
            difficulty+=50;
            stage++;
            ship.reload();
            freemiss=null;
            ship.setx(Controller.SIZE);
            return true;
        }
        else return false;
    }
    public void skip()
    {
        list.clear();
        difficulty+=50;
        stage++;
        if(ship!=null) ship.reload();
        else ship=new Ship();
        generator();
    }
    public boolean hit()
    {
        if(pause) return false;
        for(Attacker a:list)
        {
            if(a.gety()>=Ship.HEIGHT-50-Ship.radius-a.getr())
            {
                lives=1;
                return true;
            }
        }
        int[][] rockets=getMissiles();
        Missile missile;
        if (rockets!=null)
        {
            for (int i = 0; i < list.size(); i++)
            {
                if (rockets == null) break;
                for (int j = 0; j < rockets.length; j++)
                {
                    if (Math.sqrt(((list.get(i).getx()-rockets[j][0])*(list.get(i).getx()-rockets[j][0])+(list.get(i).gety()-rockets[j][1])*(list.get(i).gety()-rockets[j][1])))<=list.get(i).getr())
                    {
                        list.get(i).hit();
                        if (list.get(i).isgone())
                        {
                            for(int k=0; k<list.get(i).gets(); k++)
                            {
                                missile=list.get(i).extract(k);
                                if(missile==null)
                                {
                                    k++;
                                    continue;
                                }
                                if(freemiss==null) freemiss=new Vector<>();
                                freemiss.add(missile);
                            }
                            score+=list.get(i).getscore();
                            list.remove(i);
                        }
                        ship.shotdown(j);
                        rockets = getMissiles();
                        break;
                    }
                }
            }
        }
        int i=sumrock();
        if(i<=0) return false;
        rockets=new int[i+((freemiss==null)?0:freemiss.size())][4];
        System.arraycopy(getRockets(), 0, rockets, 0, i);
        if(freemiss!=null) System.arraycopy(getfree(), 0, rockets, i, freemiss.size());
        if(ship!=null)
        {
            for(int[] j: rockets)
            {
                if(Math.sqrt((ship.getx()-j[0])*(ship.getx()-j[0])+(Ship.HEIGHT-j[1])*(Ship.HEIGHT-j[1]))<=Ship.radius)
                {
                    return true;
                }
            }
        }
        return false;
    }
    public void shotdown()
    {
        lives--;
        ship=null;
        for (Attacker a : list) a.reload();
        freemiss=null;
    }
    private int sumrock() {int i=0; for(Attacker a:list) i+=a.gets(); return i;}
    public void pause() {pause=!pause; if(ship==null) ship=new Ship(); else if(!pause) ship.fullstop();}
    public boolean getpause() {return pause;}
    public void moveship(int i, int j) {if(ship!=null&&!pause) ship.movement(i, j);}
    public void shoot(int i) {if(pause&&i!=0) {pause();} else if(ship!=null) ship.shoot(i);}
    public int getlives() {return lives;}
    public int getscore() {return (int) score;}
    public int getstage() {return stage;}
    private static class Ship
    {
        public static final int radius;
        public static final int HEIGHT;
        private int crdx, shot, velocity, counter;
        private Vector<Missile> rockets;
        private boolean left, right;
        static
        {
            HEIGHT=Controller.SIZE*6/5-75;
            radius=10;
        }
        Ship()
        {
            crdx=Controller.SIZE;
            shot=0;
            velocity=0;
            rockets=new Vector<>();
            counter=0;
            left=false;
            right=false;
        }
        public int getx() {return crdx;}
        public int gets() {return  shot;}
        public void setx(int x) {crdx=x;}
        public void reload() {rockets.clear(); shot=0;}
        public void fullstop() {left=false; right=false; velocity=0; counter=0;}
        public Missile extract(int k)
        {
            if((shot==0)||(k>=shot)) return null;
            return rockets.get(k);
        }
        public void shotdown(int i)
        {
            if(i>=shot) return;
            rockets.remove(i);
            shot--;
        }
        public void movement(int i, int j)
        {
            if (j==-1)
            {
                left=true;
                if(right) velocity=0;
                else velocity=-4;
            }
            else if(j==1)
            {
                right=true;
                if(left) velocity=0;
                else velocity=4;
            }
            else if(j==-2)
            {
                if(right) velocity=4;
                else velocity=0;
                left=false;
            }
            else if(j==2)
            {
                if(left) velocity=-4;
                else velocity=0;
                right=false;
            }
            else if(j==4)
            {
                if(counter!=0) counter++;
                for (int k=0; k<shot; k++)
                {
                    rockets.get(k).movement(i);
                    if(rockets.get(k).gety()<=0)
                    {
                        rockets.remove(k);
                        k--;
                        shot--;
                    }
                }
                if (!((velocity<=0)&&(crdx <= radius))&&!((velocity>=0)&&(crdx>=Controller.SIZE*2-radius*3))) crdx+=velocity*i;
            }
        }
        public void shoot(int i)
        {
            if(i==1) {if(counter%30==0) counter=1;}
            else if(i==0)
            {
                counter=0;
                return;
            }
            if(counter%30==1)
            {
                rockets.add(new Missile(crdx, HEIGHT + 1, false));
                counter++;
                shot++;
            }
        }
    }
    private static class Missile
    {
        protected int crdx, crdy;
        protected boolean hostile;
        protected static final int velocity=5;
        Missile(){}
        Missile(int x, int y, boolean b)
        {
            crdx=x;
            crdy=y;
            hostile=b;
        }
        public void movement(int i)
        {
            if(hostile) crdy+=velocity*i;
            else crdy-=velocity*i;
        }
        public int getx() {return crdx;}
        public int gety() {return crdy;}
    }
    private static class Vertmissile extends Missile
    {
        private double xvelocity, yvelocity, counter1, counter2;
        private boolean xdirection;
        Vertmissile(int x, int y, boolean b, double ratio, boolean dir)
        {
            crdx=x;
            crdy=y;
            hostile=b;
            counter1=0;
            counter2=0;
            xvelocity=Math.sqrt(velocity*velocity/(1+ratio*ratio));
            yvelocity=Math.sqrt(velocity*velocity-xvelocity*xvelocity);
            xdirection=dir;
        }
        public void movement(int i)
        {
            counter2+=yvelocity*i;
            if(hostile) crdy+=counter2;
            else crdy-=(int) counter2;
            counter2-=(int) counter2;
            counter1+=xvelocity*i;
            if(xdirection) crdx+=counter1;
            else crdx-=(int) counter1;
            counter1-=(int) counter1;
        }
    }
    private abstract static class Attacker
    {
        protected Missile[] rockets;
        protected int spritenum;
        protected int crdx;
        protected int crdy;
        protected int health;
        protected boolean direction;
        protected int limitr;
        protected int limitl;
        protected int shot;
        Attacker(){}
        Attacker(int x, int y, int ll, int lr)
        {
            shot=0;
            crdx=x;
            crdy=y;
            limitr=lr;
            limitl=ll;
        }
        public int getx() {return crdx;}
        public int gety() {return crdy;}
        public int gets() {return shot;}
        public int getspr() {return spritenum;}
        public void hit() {health--;}
        public abstract int getr();
        public abstract double getscore();
        public abstract void reload();
        public abstract void movement(int i);
        public boolean isgone() {return health<=0;}
        public void shoot()
        {
            if(shot>=rockets.length) return;
            rockets[shot]=new Missile(crdx, crdy, true);
            shot++;
        }
        public Missile extract(int k)
        {
            if((shot==0)||(k>=shot)) return null;
            return rockets[k];
        }
        protected void moverocket(int i)
        {
            if(shot==0) return;
            for(int j=0; j<shot; j++)
            {
                rockets[j].movement(i);
                if(rockets[j].gety()>=Ship.HEIGHT+Ship.radius)
                {
                    if(shot-1==j)
                    {
                        rockets[j]=null;
                        shot--;
                        return;
                    }
                    if (shot-1-j>=0) System.arraycopy(rockets,j+1, rockets, j,shot-1-j);
                    rockets[shot-1]=null;
                    shot--;
                }
            }
        }
    }
    private static class SmallFry extends Attacker
    {
        public static final int radius;
        public static final double weight;
        private static final int velocity;
        static
        {
            radius=25;
            weight=0.1;
            velocity=1;
        }
        SmallFry(int x, int y, int ll, int lr)
        {
            super(x, y, ll ,lr);
            rockets=new Missile[1];
            direction=true;
            spritenum=1;
            health=1;
        }
        public void movement(int i)
        {
            if(direction) crdx+=i*velocity;
            else crdx-=i*velocity;
            if(crdx-radius<=limitl)
            {
                direction=(!direction);
                crdx=limitl+radius+1;
                crdy+=radius*2;
            }
            if(crdx+radius>=limitr)
            {
                direction=(!direction);
                crdx=limitr-radius-1;
                crdy+=radius*2;
            }
            moverocket(i);
        }
        public void reload() {shot=0; rockets=new Missile[1];}
        public int getr(){return radius;}
        public double getscore(){return 10*weight;}
    }
    private static class Runner extends Attacker
    {
        public static final int radius;
        public static final double weight;
        private static final int velocity;
        static
        {
            radius=20;
            weight=0.2;
            velocity=2;
        }
        Runner(int x, int y, int ll, int lr)
        {
            super(x, y, ll ,lr);
            rockets=new Missile[2];
            direction=true;
            spritenum=2;
            health=1;
        }
        public void movement(int i)
        {
            if(direction) crdx+=i*velocity;
            else crdx-=i*velocity;
            if(crdx-radius<=limitl)
            {
                direction=(!direction);
                crdx=limitl+radius+1;
            }
            if(crdx+radius>=limitr)
            {
                direction=(!direction);
                crdx=limitr-radius-1;
            }
            moverocket(i);
        }
        public void reload() {shot=0; rockets=new Missile[2];}
        public int getr(){return radius;}
        public double getscore(){return 10*weight;}
    }
    private static class Abnormal extends Attacker
    {
        public static final int radius;
        public static double weight;
        private static final int velocity;
        private int counter, magazine;
        static
        {
            radius=25;
            weight=0.33;
            velocity = 1;
        }
        Abnormal(int x, int y, int ll, int lr, boolean b)
        {
            super(x, y, ll, lr);
            rockets=new Missile[3];
            spritenum=3;
            health=3;
            counter=0;
            magazine=0;
            direction=b;
        }
        public void movement(int i)
        {
            counter+=i;
            if(counter==7)
            {
                crdy++;
                counter=0;
            }
            if (direction) crdx+=i*velocity;
            else crdx-=i*velocity;
            if(crdx-radius<=limitl)
            {
                direction=(!direction);
                crdx=limitl+radius+1;
            }
            if(crdx+radius>=limitr)
            {
                direction=(!direction);
                crdx=limitr-radius-1;
            }
            magazine+=i;
            if(magazine>=50) shoot();
            moverocket(i);
        }
        public void shoot()
        {
            magazine=0;
            if(shot>0) return;
            rockets[2]=new Vertmissile(crdx, crdy, true, 1, direction);
            rockets[1]=new Vertmissile(crdx, crdy, true, 1, !direction);
            rockets[0]=new Missile(crdx, crdy, true);
            shot=3;
        }
        public void reload() {shot=0; rockets=new Missile[3]; magazine=0;}
        public int getr(){return radius;}
        public double getscore(){return 10*weight;}
    }
    private static class Thiccboy extends Attacker
    {
        public static final int radius;
        public static final double weight;
        private static final int velocity;
        private int magazine;
        static
        {
            radius=40;
            weight=1.2;
            velocity=1;
        }
        Thiccboy(int x, int y, int ll, int lr)
        {
            super(x, y, ll ,lr);
            magazine=0;
            rockets=new Missile[6];
            direction=true;
            spritenum=4;
            health=10;
        }
        public void movement(int i)
        {
            if(direction) crdx+=i*velocity;
            else crdx-=i*velocity;
            if(crdx-radius<=limitl)
            {
                direction=(!direction);
                crdx=limitl+radius+1;
                crdy+=radius*2;
            }
            if(crdx+radius>=limitr)
            {
                direction=(!direction);
                crdx=limitr-radius-1;
                crdy+=radius*2;
            }
            magazine+=i;
            if(magazine>=50) shoot();
            moverocket(i);
        }
        public void shoot()
        {
            if(magazine>0) magazine=0;
            if(shot>0) return;
            rockets[0]=new Missile(crdx+radius/4, crdy+radius, true);
            rockets[1]=new Missile(crdx-radius/4, crdy+radius, true);
            rockets[2]=new Missile(crdx-radius/4, crdy, true);
            rockets[3]=new Missile(crdx+radius/4, crdy, true);
            rockets[4]=new Vertmissile(crdx+radius/4, crdy, true, 2, direction);
            rockets[5]=new Vertmissile(crdx-radius/4, crdy, true, 2, !direction);
            shot+=6;
        }
        public void reload() {shot=0; rockets=new Missile[6]; magazine=0;}
        public int getr(){return radius;}
        public double getscore(){return 10*weight;}
    }
    private static class Hivewitch extends Attacker
    {
        public static final int radius;
        public static final double weight;
        private static final int velocity;
        private int magazine, mandate;
        static
        {
            radius=40;
            weight=2;
            velocity=1;
        }
        Hivewitch(int x, int y, int ll, int lr)
        {
            super(x, y, ll, lr);
            rockets=null;
            direction=true;
            spritenum=5;
            health=10;
            mandate=200;
        }
        public void movement(int i)
        {
            if(direction) crdx+=i*velocity;
            else crdx-=i*velocity;
            if(crdx-radius<=limitl)
            {
                direction=(!direction);
                crdx=limitl+radius+1;
            }
            if(crdx+radius>=limitr)
            {
                direction=(!direction);
                crdx=limitr-radius-1;
            }
            mandate-=i;
            magazine+=i;
            if(magazine>=400) shoot();
        }
        public void shoot()
        {
            if(mandate>0) return;
            mandate=200;
            magazine=0;
            queue.add(new SmallFry(crdx+radius*2, crdy+radius*2, (0>crdx-SmallFry.radius*10)?0:crdx-SmallFry.radius*10, (Controller.SIZE*2<crdx+SmallFry.radius*10)?Controller.SIZE*2:crdx+SmallFry.radius*10));
            queue.add(new SmallFry(crdx-radius*2, crdy+radius*2, (0>crdx-SmallFry.radius*10)?0:crdx-SmallFry.radius*10, (Controller.SIZE*2<crdx+SmallFry.radius*10)?Controller.SIZE*2:crdx+SmallFry.radius*10));
        }
        public void reload() {}
        public int getr(){return radius;}
        public double getscore(){return 10*weight;}
    }
    private static class Laserboy extends Attacker
    {
        public static final int radius;
        public static final double weight;
        private static final int velocity;
        private int magazine, counter;
        static
        {
            radius=30;
            weight=0.75;
            velocity=1;
        }
        Laserboy(int x, int y, int ll, int lr, boolean b)
        {
            super(x, y, ll, lr);
            magazine=0;
            rockets=null;
            direction=b;
            spritenum=6;
            health=5;
            counter=0;
        }
        public void movement(int i)
        {
            if(direction) crdx+=i*velocity;
            else crdx-=i*velocity;
            if(crdx-radius<=limitl)
            {
                direction=(!direction);
                crdx=limitl+radius+1;
            }
            if(crdx+radius>=limitr)
            {
                direction=(!direction);
                crdx=limitr-radius-1;
            }
            if(spritenum==6) magazine+=i;
            else counter+=i;
            if(magazine>=200) shoot();
            if(counter>=150)
            {
                if(spritenum==-6)
                {
                    spritenum=-66;
                    shot=3;
                }
                else
                {
                    spritenum=6;
                    shot=0;
                }
                counter=0;
            }
        }
        public void shoot()
        {
            magazine=0;
            if(spritenum==6)
            {
                spritenum=-6;
            }
        }
        public Missile extract(int k)
        {
            if(spritenum!=-66) return null;
            return new Missile(crdx+(k-1)*radius/2, Ship.HEIGHT, true);
        }
        public void reload() {magazine=0; counter=0; spritenum=6; shot=0;}
        public int getr(){return radius;}
        public double getscore(){return 10*weight;}
    }
    private static class Lamp extends Attacker
    {
        public static final int radius;
        public static final double weight;
        private static final int velocity;
        private static final int orbit;
        private Moth[] minions=new Moth[3];
        private int magazine, startx, starty;
        private double counter1, counter2, angle;
        static
        {
            radius=30;
            weight=2;
            velocity=1;
            orbit=175;
        }
        Lamp(int x, int y, boolean b, double a)
        {
            magazine=0;
            direction=b;
            spritenum=7;
            crdx=(int) (x+(orbit)*Math.cos(a));
            startx=x;
            crdy=(int) (y+(orbit)*Math.sin(a));
            starty=y;
            health=5;
            counter1=x+(orbit)*Math.cos(a)-crdx;
            counter2=y+(orbit)*Math.sin(a)-crdy;
            angle=a;
            for(int i=0; i<3; i++)
            {
                minions[i]=new Moth(crdx, crdy, (150+i*120)*Math.PI/180, !direction, Lamp.radius+Moth.radius);
                list.add(minions[i]);
            }
        }
        public void movement(int i)
        {
            angle+=(direction)?i*velocity*Math.PI/180:-i*velocity*Math.PI/180;
            counter1+=startx-crdx+(orbit)*Math.cos(angle);
            counter2+=starty-crdy+(orbit)*Math.sin(angle);
            if(counter1>=1||counter1<=-1)
            {
                crdx+=(int) counter1;
                counter1-=(int) counter1;
            }
            if(counter2>=1||counter2<=-1)
            {
                crdy+=(int) counter2;
                counter2-=(int) counter2;
            }
            for(Moth m:minions)
            {
                m.movement(crdx, crdy);
                if(m.isgone()) m.movement(i);
            }
            magazine+=i;
            if(magazine>=100) shoot();
        }
        public void shoot()
        {
            magazine=0;
            for(Moth m: minions)
            {
                if(m.isgone()) m.res();
                m.shoot();
            }
        }
        public void reload() {}
        public int getr(){return radius;}
        public double getscore(){return 10*weight;}
    }
    private static class Moth extends Attacker
    {
        public static final double weight;
        public static final int radius;
        private static final int velocity;
        private double counter1, counter2, angle;
        private int startx, starty, orbit;
        static
        {
            radius=15;
            weight=0;
            velocity=1;
        }
        Moth(int x, int y, double a, boolean b, int o)
        {
            rockets=new Missile[2];
            direction=b;
            spritenum=8;
            orbit=o;
            crdx=(int) (x+(orbit)*Math.cos(a));
            startx=x;
            crdy=(int) (y+(orbit)*Math.sin(a));
            shot=0;
            starty=y;
            health=1;
            counter1=x+(orbit)*Math.cos(a)-crdx;
            counter2=y+(orbit)*Math.sin(a)-crdy;
            angle=a;
        }
        public void movement(int i)
        {
            angle+=(direction)?i*velocity*Math.PI/180:-i*velocity*Math.PI/180;
            counter1+=startx-crdx+(orbit)*Math.cos(angle);
            counter2+=starty-crdy+(orbit)*Math.sin(angle);
            crdx+=counter1;
            counter1-=counter1;
            crdy+=counter2;
            counter2-=(int) counter2;
            if(!isgone()) moverocket(i);
        }
        public void res() {health=1;queue.add(this); reload();}
        public void movement(int x, int y)
        {
            startx+=x-startx;
            starty+=y-starty;
        }
        public void reload() {shot=0; rockets=new Missile[2];}
        public int getr(){return radius;}
        public double getscore(){return 10*weight;}
    }
    private static class Glitch extends Attacker
    {
        public static final int radius;
        public static final double weight;
        private int magazine;
        private double counter;
        private int evolve() {counter=(counter*seed[0]*947*109/107+counter*seed[0]*947*109/107)%(5*Controller.SIZE); return (int) counter;}
        static
        {
            radius=25;
            weight=0.25;
        }
        Glitch(int x, int y, double ctr)
        {
            super(x, y, 0, 0);
            rockets=new Missile[3];
            spritenum=9;
            health=3;
            counter=ctr;
            magazine=0;
        }
        public void movement(int i)
        {
            magazine+=i;
            if(magazine>=0) spritenum=-9;
            if(magazine>=100) spritenum=9;
            if(magazine>=450) spritenum=-9;
            if(magazine==475||magazine==10||magazine==495||magazine==60) spritenum=0;
            if(magazine>=500)
            {
                crdx=evolve()%(Controller.SIZE*2-2*radius)+radius;
                crdy=evolve()%(Controller.SIZE*6/5-300)+radius;
                shoot();
                magazine=0;
            }
            moverocket(i);
        }
        public void shoot()
        {
            if(magazine<=250) magazine=0;
            if(shot>=rockets.length) return;
            rockets[shot]=new Vertmissile(crdx, crdy, true, Math.abs(((double) Ship.HEIGHT-(double) crdy)/((double) ship.getx()-(double) crdx)), ship.getx()>crdx);
            shot++;
        }
        public void reload() {shot=0; rockets=new Missile[1];}
        public int getr(){return radius;}
        public double getscore(){return 10*weight;}
    }
    private static class Gargantua extends Attacker
    {
        public static final int radius;
        public static double weight;
        private enum Attack {hive, laser, barrage, blank}
        Attack attack;
        private Vector<Missile> rockets;
        private static int velocity;
        private double random;
        private int magazine, center, counter, countdown, minions;
        private int evolve() {random=(random*seed[0]*947*109/107+random*seed[0]*947*109/107)%5000; return (int) random;}
        static
        {
            radius=100;
            weight=100;
            velocity=2;
        }
        private void hive(int i)
        {
            counter+=i;
            velocity=0;
            if(counter>=50)
            {
                queue.add(new SmallFry(crdx, crdy+radius+SmallFry.radius, crdx-10*SmallFry.radius, crdx+10*SmallFry.radius));
                counter=0;
                minions++;
            }
            if(minions>=10)
            {
                minions=0;
                counter=0;
                attack=Attack.blank;
                velocity=2;
            }
        }
        private void barrage(int i)
        {
            velocity=0;
            if(evolve()%100>=85)
            {
                rockets.add(new Vertmissile(crdx+evolve()%(radius*2+1)-radius, crdy, true, 5000/(double) evolve(),evolve()%2==1));
                shot++;
            }
            counter+=i;
            if(counter>=500)
            {
                counter=0;
                attack=Attack.blank;
                velocity=2;
            }
        }
        private void laser(int i)
        {
            velocity=0;
            magazine=0;
            if(spritenum==10) counter+=i;
            else countdown+=i;
            if(counter>=50&&spritenum==10)
            {
                spritenum=-10;
                counter=0;
            }
            if(countdown>=100)
            {
                if(spritenum==-10)
                {
                    spritenum=-1010;
                    shot+=12;
                }
                else
                {
                    spritenum=10;
                    attack=Attack.blank;
                    velocity=2;
                    shot-=12;
                }
                countdown=0;
            }
        }
        Gargantua(int x, int y, int ll, double ctr)
        {
            super(x, y, ll, x-ll+radius);
            rockets=new Vector<>();
            spritenum=10;
            health=100;
            random=ctr;
            magazine=0;
            center=x;
            direction=true;
            counter=0;
            attack=Attack.blank;
            minions=0;
            countdown=0;
        }
        public void movement(int i)
        {
            if(crdy<=limitl&&crdx>=center)
            {
                crdy+=velocity*i;
                crdx+=velocity*i;
            }
            else if(crdy<=limitl)
            {
                crdy-=velocity*i;
                crdx+=velocity*i;
            }
            else if (crdx>=limitr) crdx-=velocity*3/2*i;
            else
            {
                crdy-=i*velocity*2;
                crdx+=i*velocity*2;
            }
            magazine+=i;
            if(magazine>=50) shoot();
            moverocket(i);
            if(attack==Attack.blank) counter+=i;
            if(counter>=(evolve()%500+250))
            {
                counter=0;
                switch(evolve()%3)
                {
                    case 0:
                        attack = Attack.hive;
                        break;

                    case 1:
                        attack=Attack.barrage;
                        break;
                    case 2:
                         attack=Attack.laser;
                        break;
                }
            }
            if(attack==Attack.hive) hive(i);
            else if(attack==Attack.barrage) barrage(i);
            else if(attack==Attack.laser) laser(i);
        }
        public void shoot()
        {
            magazine=0;
            rockets.add(new Missile(crdx, crdy, true));
            shot++;
        }
        public Missile extract(int k)
        {
            if(shot==0||(k>=shot&&attack!=Attack.laser)) return null;
            if(k<rockets.size()) return rockets.get(k);
            return new Missile(crdx-radius+(k-rockets.size())*((int) (radius/7))+((int) (k/3))*((int) (radius/7)), Ship.HEIGHT, true);
        }
        protected void moverocket(int i)
        {
            if(shot==0) return;
            for(int j=0; j<rockets.size(); j++)
            {
                rockets.get(j).movement(i);
                if(rockets.get(j).gety()>=Ship.HEIGHT+Ship.radius)
                {
                    rockets.remove(j);
                    j--;
                    shot--;
                    continue;
                }
            }
        }
        public void reload() {shot=0; rockets.clear(); magazine=0; counter=0; attack=Attack.blank; countdown=0; velocity=2; spritenum=10;}
        public int getr(){return radius;}
        public double getscore(){return 10*weight;}
    }
}
