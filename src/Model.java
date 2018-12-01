import java.util.*;

/**
 * Model w modelu MVC. Ma w sobie dane gry, oraz dokonuje na nich manipulacji.
 * pause jest zmienna boolean mowiaca czy jest pauza. difficulty sluzy
 * generatorowi poziomow do generacji poziomow. modifier sluzy do generacji
 * proceduralnej liczb odpowiadajacych za strzelanie przeciwnikow. ship jest
 * statkiem gracza. seed jest seedem generatora poziomow. list jest lista
 * przeciwnikow. freemiss jest lista pociskow nieprzypisanych do przeciwnika.
 * queue jest lista przeciwnikow czekajacych na dodanie do listy zasadniczej.
 */
class Model
{
    private boolean pause;
    private int difficulty, lives, stage;
    private double score, modifier;
    private static Ship ship;
    private static double[] seed;
    private static Vector<Attacker> list;
    private Vector<Missile> freemiss;
    private static List<Attacker> queue;

    /**
     * konstuktor Modelu ustawia warunki poczatkowe, liczbe zyc, wynik, poziom,
     * "ilosc" trudnosci, losuje seed, oraz ustawia zmienna modifier sluzaca do
     * proceduralnego generowania liczb odpowiadajacych za strzelanie.
     */
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
        freemiss=new Vector<>();
        queue=new Vector<>();
    }

    /**
     * funkcja pomocnicza konstruktora, genruje losowa liczbe z zakresu (min;max)
     * @param min jedna z granic zakresu
     * @param max jedna z granic zakresu
     * @return liczba losowa
     */
    private double randfrom(double min, double max) {return (Math.random()*Math.abs(max-min))+((min<=max)?min:max);}

    /**
     * funkcja pomocnicza generatora, sluzy do generacji liczb pseudolosowych z zakresu
     * (seed[0]/2;seed[0]*3/2).
     * @return liczba pseudolosowa z zakresu (2/3;2)
     */
    private double evolve() {seed[10]=(seed[10]*(seed[0]*947*109/107)+(seed[0]*947*109/107))%seed[0]+seed[0]/2; return seed[0]/seed[10];}

    /**
     * setter seedu
     * @param arr nowy seed
     */
    void loadseed(double[] arr){seed=arr;}

    /**
     * generator poziomow dzialajacy na podstawie liczb pseudolosowych.
     */
    void generator()
    {
        int y, x, smallfries, runners, abnormals, thiccboyes, hivewitches, laserboys, lamps, glitches;
        double e;
        int[] quantity = new int[10];
        int rows;
        list.clear();
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

    /**
     * generator poziomu z bossem
     */
    void boss()
    {
        list.add(new Gargantua(Controller.SIZE, Gargantua.radius,500, seed[0]/seed[10]));
    }

    /**
     * zwraca pozycje przeciwnikow i gracza, oraza ich numery modeli, w postaci tablicy intow
     * @return pozycje gracza i przeciwnikow zakodowane za pomoca tablicy intow
     */
    int[][] getPositions()
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

    /**
     * getter pozycji pociskow przeciwnikow i pociskow "wolnych"
     * @return pozycje pociskow przeciwnikow w postaci tablicy intow
     */
    int[][] getRockets()
    {
        if(pause) return null;
        int k=0, sum=sumrock();
        Missile missile;
        int[][] data=new int[sum+freemiss.size()][4];
        for(Attacker a: list)
        {
            for (int j=0; j<a.gets(); j++)
            {
                if (a.extract(j)!=null)
                {
                    missile=a.extract(j);
                    data[k][0]=missile.getx();
                    data[k][1]=missile.gety();
                    data[k][2]=1;
                    if(missile.gety()==Ship.HEIGHT) data[k][3]=-5000;
                    else data[k][3]=-2;
                    k++;
                }
            }
        }
        for(int i=0; i<freemiss.size(); i++)
        {
            data[sum+i][0]=freemiss.get(i).getx();
            data[sum+i][1]=freemiss.get(i).gety();
            data[sum+i][2]=1;
            data[sum+i][3]=-2;
        }
        return data;
    }

    /**
     * getter pozycji pociskow gracza
     * @return pozycje pociskow gracza w postaci tablicy intow
     */
    int[][] getShipRockets()
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

    /**
     * zasadnicza funkcja ruchu, rusza kazdy obiekt w modelu
     * @param i dlugosc kroku
     */
    void movement(int i)
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
        if(!freemiss.isEmpty())
        {
            for(int j=0; j<freemiss.size(); j++)
            {
                freemiss.get(j).movement(i);
                if(freemiss.get(j).gety()>=Ship.HEIGHT+Ship.radius)
                {
                    freemiss.remove(j);
                    j--;
                }
                if(freemiss.isEmpty()) break;
            }
        }
        if(ship!=null) ship.movement(i);
    }

    /**
     * sprawdza czy zwyciestwo osiagniate
     * @return czy gracz juz wygral
     */
    boolean checkWin()
    {
        if(list.isEmpty())
        {
            difficulty+=25;
            stage++;
            ship.reload();
            freemiss.clear();
            ship.setx(Controller.SIZE);
            return true;
        }
        else return false;
    }

    /**
     * NIE NALEZY UZYWAC W OSTATECZNEJ WERSJI
     * funkcja do przeciazania generatora poziomow
     */
    void skip()
    {
        list.clear();
        difficulty+=25;
        stage++;
        if(ship!=null) ship.reload();
        else ship=new Ship();
        freemiss.clear();
        generator();
    }

    /**
     * sprawdza kolizje z pociskami u gracza i przeciwnikow
     * @return czy gracz zostal trafiony
     */
    boolean hit()
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
        int[][] rockets=getShipRockets();
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
                                freemiss.add(missile);
                            }
                            score+=list.get(i).getscore();
                            list.remove(i);
                        }
                        ship.hit(j);
                        rockets = getShipRockets();
                        break;
                    }
                }
            }
        }
        int i=sumrock();
        if(i<=0) return false;
        rockets=getRockets();
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

    /**
     * gracz zostal trafiony
     */
    void shotdown()
    {
        lives--;
        ship=null;
        for (Attacker a : list) a.reload();
        freemiss.clear();
    }

    /**
     * ile pociskow jest obecnie w grze
     * @return ilosc pociskow w grze
     */
    private int sumrock() {int i=0; for(Attacker a:list) i+=a.gets(); return i;}

    /**
     * zmien stan pauzy
     */
    void pause() {pause=!pause; if(ship==null) ship=new Ship(); else if(!pause) ship.fullstop();}

    /**
     * getter stanu pauzy
     * @return stan pauzy
     */
    boolean getpause() {return pause;}

    /**
     * logika statku na zasadzie automatu stanow
     * @param j klikniecie strzalki (<0 to w lewo, >0 to w prawo, 1 to klik, 2 to release)
     */
    void logic(int j) {if(ship!=null&&!pause) ship.logic(j);}

    /**
     * strzal statku
     * @param b true - spacja kliknieta, false - spacja odkliknieta
     */
    void shoot(boolean b) {if(pause&&b) {pause();} else if(ship!=null) ship.shoot(b);}

    /**
     * getter zyc
     * @return liczba zyc
     */
    int getlives() {return lives;}

    /**
     * getter wyniku
     * @return wynik
     */
    int getscore() {return (int) score;}

    /**
     * getter poziomu
     * @return poziom
     */
    int getstage() {return stage;}

    /**
     * statek gracza, ruch odbywa sie na zasadzie automatu stanow wyznaczanego przez dwa bity: left i right, na podstawie ktorego
     * odczytywana jest predkosc, strzal na zasadzie timera counter.
     */
    private static class Ship
    {
        /**
         * rozmiar hitboxa statku
         */
        static final int radius;
        /**
         * wysokosc na jakiej znajduje sie statek
         */
        static final int HEIGHT;
        private double crdx, velocity;
        private int counter;
        private Vector<Missile> rockets;
        private boolean left, right;
        static
        {
            HEIGHT=(int)(Controller.SIZE*Controller.RATIO)-75;
            radius=(int)(10*Controller.SCALE);
        }

        /**
         * konstruktor statku, ustawia warunki poczatkowe i ustawia statek na srodku ekranu.
         */
        Ship()
        {
            crdx=Controller.SIZE;
            velocity=0;
            rockets=new Vector<>();
            counter=0;
            left=false;
            right=false;
        }

        /**
         * getter wspolrzednej x
         * @return wspolrzedna x
         */
        int getx() {return (int) crdx;}

        /**
         * getter liczby wystrzelonych pociskow
         * @return lcizba wystrzelonych pociskow
         */
        int gets() {return  rockets.size();}

        /**
         * setter wspolrzednej x
         * @param x nowa wspolrzedna x
         */
        void setx(int x) {crdx=x;}

        /**
         * wyczyszczenie pociskow gracza
         */
        void reload() {rockets.clear();}

        /**
         * reset statku do warunkow poczatkowych
         */
        void fullstop() {left=false; right=false; velocity=0; counter=0;}

        /**
         * wyciagniecie pocisku z pozycji
         * @param k pozycja na liscie
         * @return obiekt typu Missile ktory sie tam znajduje
         */
        Missile extract(int k)
        {
            if((rockets.isEmpty())||(k>=rockets.size())) return null;
            return rockets.get(k);
        }

        /**
         * pocisk o indeksie i trafil w cel, nalezy sie go pozbyc
         * @param i indeks na liscie
         */
        void hit(int i)
        {
            if(i>=rockets.size()) return;
            rockets.remove(i);
        }

        /**
         * automat stanow ruchu statku
         * @param j id eventu: <0 w lewo, >0 w prawo, 1 - wcisnieto, 2 - wypuszcono
         */
        void logic(int j)
        {
            if (j==-1)
            {
                left=true;
                if(right) velocity=0;
                else velocity=-4*Controller.SCALE;
            }
            else if(j==1)
            {
                right=true;
                if(left) velocity=0;
                else velocity=4*Controller.SCALE;
            }
            else if(j==-2)
            {
                if(right) velocity=4*Controller.SCALE;
                else velocity=0;
                left=false;
            }
            else if(j==2)
            {
                if(left) velocity=-4*Controller.SCALE;
                else velocity=0;
                right=false;
            }
        }

        /**
         * zasadnicza funkcja ruchu statku wywolywana przez petle
         * @param i dlugosc kroku
         */
        void movement(int i)
        {
            if(counter%30==1)
            {
                rockets.add(new Missile(crdx, HEIGHT + 1, false));
                counter++;
            }
            if(counter!=0) counter++;
            for (int k=0; k<rockets.size(); k++)
            {
                rockets.get(k).movement(i);
                if(rockets.get(k).gety()<=0)
                {
                    rockets.remove(k);
                    k--;
                }
            }
            if (!((velocity<=0)&&(crdx <= radius))&&!((velocity>=0)&&(crdx>=Controller.SIZE*2-radius*3))) crdx+=velocity*i;
        }

        /**
         * wystrzal pocisku
         * @param b true - spacja wcisnieta, false - wypuszczona
         */
        void shoot(boolean b)
        {
            if(b) {if(counter%30==0) counter=1;}
            else counter=0;
        }
    }

    /**
     * pocisk wystrzelony prosto przez gracza badz przeciwnika, zwiera w sobie swoje
     * wspolrzedne, dane czy jest przyjazny i swoja predkosc.
     */
    private static class Missile
    {
        protected double crdx, crdy;
        protected boolean hostile;
        protected static final double velocity=5*Controller.SCALE;
        Missile(){}

        /**
         * konstruktor ustawia dane tak jak mu zostana podane
         * @param x wspolrzedna x
         * @param y wpolsrzedna y
         * @param b czy jest wrogi graczowi
         */
        Missile(double x, double y, boolean b)
        {
            crdx=x;
            crdy=y;
            hostile=b;
        }

        /**
         * zasadnicza funkcja ruchu pocisku
         * @param i dlugosc kroku
         */
        public void movement(int i)
        {
            if(hostile) crdy+=velocity*i;
            else crdy-=velocity*i;
        }

        /**
         * getter wspolrzednej x
         * @return wpolrzedna x
         */
        int getx() {return (int)crdx;}

        /**
         * getter wspolrzednej y
         * @return wspolrzedna y
         */
        int gety() {return (int)crdy;}
    }

    /**
     * pocisk wystrzelony diagonalnie przez przeciwnika badz gracza(nie zdarza sie), dziecko
     * prostego pocisku, zawiera dodatkowo predkosc w wymiarze x, y oraz kierunek w wymiarze x.
     */
    private static class Diagmissile extends Missile
    {
        private double xvelocity, yvelocity;
        private boolean xdirection;

        /**
         * konstruktor ustawia dane tak jak mu zostarna podane, a nastepnie za pomoca argumentu
         * ratio oblicza predkosc w wymiarze x i y
         * @param x wspolrzedna x
         * @param y wspolrzedna y
         * @param b czy jest wrogi
         * @param ratio stosunek predkosci y do predkosci x
         * @param dir kierunek w x
         */
        Diagmissile(double x, double y, boolean b, double ratio, boolean dir)
        {
            crdx=x;
            crdy=y;
            hostile=b;
            xvelocity=Math.sqrt(velocity*velocity/(1+ratio*ratio));
            yvelocity=Math.sqrt(velocity*velocity-xvelocity*xvelocity);
            xdirection=dir;
        }

        /**
         * zasadnicza funkcja ruchu pocisku
         * @param i dlugosc kroku
         */
        public void movement(int i)
        {
            if(hostile) crdy+=yvelocity*i;
            else crdy-=yvelocity*i;
            if(xdirection) crdx+=xvelocity*i;
            else crdx-=xvelocity*i;
        }
    }

    /**
     * Szablon kazdego przeciwnika w grze, Attacker zawiera dane i abstrakcyjne funkcje ktore kazdy
     * przeciwnik musi miec, czyli tablice pociskow, numer modelu, wspolrzedna w x i y, ilosc zycia,
     * kierunek w x, granice ruchu z prawej i lewej strony, oraz liczbe wystrzelonych pociskow.
     */
    private abstract static class Attacker
    {
        protected Missile[] rockets;
        protected double crdx, crdy;
        protected boolean direction;
        protected int spritenum, health, limitr, limitl, shot;
        Attacker(){}

        /**
         * konstruktor ustawia dane tak jak mu sie je poda
         * @param x wspolrzedna x
         * @param y wspolrzenda y
         * @param ll limit ruchu z lewej strony
         * @param lr limit ruchu z prawej strony
         */
        Attacker(int x, int y, int ll, int lr)
        {
            shot=0;
            crdx=x;
            crdy=y;
            limitr=lr;
            limitl=ll;
        }

        /**
         * getter wspolrzednej x
         * @return wspolrzedna x
         */
        int getx() {return (int)crdx;}

        /**
         * getter wspolrzednej y
         * @return wspolrzedna y
         */
        int gety() {return (int)crdy;}

        /**
         * getter liczby wystrzelonych pociskow
         * @return liczba wystrzelonych pociskow
         */
        int gets() {return shot;}

        /**
         * getter numeru modelu
         * @return numer modelu
         */
        int getspr() {return spritenum;}

        /**
         * przeciwnik zostal trafiony
         */
        void hit() {health--;}

        /**
         * abstrakcyjny getter promienia
         * @return promien dziecka
         */
        abstract int getr();

        /**
         * abstrakcyjny getter punktow za zabicie przeciwnika
         * @return liczba punktow
         */
        abstract double getscore();

        /**
         * abstrakcyjne "przeladowanie", resetuje wystrzelone pociski
         */
        abstract void reload();

        /**
         * abstrakcyjna zasadnicza funkja ruchu
         * @param i dlugosc kroku
         */
        abstract void movement(int i);

        /**
         * czy jeszcze zyje
         * @return czy jeszcze zyje
         */
        boolean isgone() {return health<=0;}

        /**
         * podstwaowa funkcja wystrzalu
         */
        void shoot()
        {
            if(shot>=rockets.length) return;
            rockets[shot]=new Missile(crdx, crdy, true);
            shot++;
        }

        /**
         * getter pocisku z pozycji k
         * @param k indeks na liscie
         * @return pocisk z pozycji k
         */
        Missile extract(int k)
        {
            if((shot==0)||(k>=shot)) return null;
            return rockets[k];
        }

        /**
         * funkcja pomocnicza ruchu, porusza rakietami
         * @param i dlugosc kroku
         */
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

    /**
     * podstawowy przeciwnik, rusza sie na boki i potem dol, ma
     * jedno zycie i na raz strzela tylko jedna rakieta
     */
    private static class SmallFry extends Attacker
    {
        /**
         * promien
         */
        static final int radius;
        /**
         * zmienna orientacyjna generatora i zasadnicza wyniku
         */
        static final double weight;
        private static final double velocity;
        static
        {
            radius=(int)(25*Controller.SCALE);
            weight=0.1;
            velocity=1*Controller.SCALE;
        }

        /**
         * konstruktor ustawia dane tak jak mu sie je poda, oraz ustala stan poczatkowy
         * @param x wspolrzedna x
         * @param y wspolrzedna y
         * @param ll limit x z lewej
         * @param lr limit x z prawej
         */
        SmallFry(int x, int y, int ll, int lr)
        {
            super(x, y, ll ,lr);
            rockets=new Missile[1];
            direction=true;
            spritenum=1;
            health=1;
        }

        /**
         * zasadnicza funkcja ruchu
         * @param i dlugosc kroku
         */
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

        /**
         * @see Attacker
         */
        public void reload() {shot=0; rockets=new Missile[1];}

        /**
         * @see Attacker
         * @return promien
         */
        public int getr(){return radius;}

        /**
         * @see Attacker
         * @return liczba punktow
         */
        public double getscore(){return 10*weight;}
    }

    /**
     * przeciwnik troche szybszy niz smallfry, rusza sie 2x szybciej i ma 2 rakiety,
     * ale nie rusza sie w dol
     */
    private static class Runner extends Attacker
    {
        /**
         * promien
         */
        static final int radius;
        /**
         * zmienna orientacyjna generatora i zasadnicza punktow
         */
        static final double weight;
        private static final double velocity;
        static
        {
            radius=(int)(20*Controller.SCALE);
            weight=0.2;
            velocity=2*Controller.SCALE;
        }

        /**
         * konstruktor ustawia dane tak jak mu sie je poda, oraz ustala stan poczatkowy
         * @param x wspolrzedna x
         * @param y wspolrzedna y
         * @param ll limit x z lewej
         * @param lr limit x z prawej
         */
        Runner(int x, int y, int ll, int lr)
        {
            super(x, y, ll ,lr);
            rockets=new Missile[2];
            direction=true;
            spritenum=2;
            health=1;
        }

        /**
         * zasadnicza funkcja ruchu
         * @param i dlugosc kroku
         */
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

        /**
         * reset rakiet
         */
        public void reload() {shot=0; rockets=new Missile[2];}

        /**
         * getter promienia
         * @return promien
         */
        public int getr(){return radius;}

        /**
         * getter punktow
         * @return liczba punktow
         */
        public double getscore(){return 10*weight;}
    }

    /**
     * Abnormal ma wiecej zycia niz Smallfry, strzela innym wzorem i porusza sie po skosie.
     * Ma dodatkowo zmienna magazine, ktora pomaga czesciej strzelac
     */
    private static class Abnormal extends Attacker
    {
        /**
         * promien
         */
        static final int radius;
        /**
         * zmienna orientacyjna generatora i zasadnicza punktow
         */
        static double weight;
        private static final double velocity;
        private int magazine;
        static
        {
            radius=(int)(26*Controller.SCALE);
            weight=0.33;
            velocity=1*Controller.SCALE;
        }

        /**
         * konstruktor ustawia dane tak jak sie poda i ustala stan poczatkowy
         * @param x wspolrzedna x
         * @param y wspolrzedna y
         * @param ll limit x z lewej
         * @param lr limit x z prawej
         * @param b kierunek w x
         */
        Abnormal(int x, int y, int ll, int lr, boolean b)
        {
            super(x, y, ll, lr);
            rockets=new Missile[3];
            spritenum=3;
            health=3;
            magazine=0;
            direction=b;
        }

        /**
         * zasadnicza funkcja ruchu
         * @param i dlugosc kroku
         */
        public void movement(int i)
        {
            crdy+=velocity*i/6;
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

        /**
         * fukcja strzalu, wystrzeliwuje jede pocisk w dol i dwa po skosie w bok
         */
        public void shoot()
        {
            magazine=0;
            if(shot>0) return;
            rockets[2]=new Diagmissile(crdx, crdy, true, 1, direction);
            rockets[1]=new Diagmissile(crdx, crdy, true, 1, !direction);
            rockets[0]=new Missile(crdx, crdy, true);
            shot=3;
        }

        /**
         * reset pociskow
         */
        public void reload() {shot=0; rockets=new Missile[3]; magazine=0;}

        /**
         * getter pormineia
         * @return promien
         */
        public int getr(){return radius;}

        /**
         * getter punktow
         * @return liczba punktow
         */
        public double getscore(){return 10*weight;}
    }

    /**
     * Thiccboy jest wiekszy niz Smallfry, ma wiecej zycia, inny wzor strzalow,
     * ale porusza sie tak samo. Ma takze magazine jak ABnormal
     */
    private static class Thiccboy extends Attacker
    {
        /**
         * promien
         */
        static final int radius;
        /**
         * zmienna orientacyjna generatora i zasadnicza punktow
         */
        static final double weight;
        private static final double velocity;
        private int magazine;
        static
        {
            radius=(int)(40*Controller.SCALE);
            weight=1.2;
            velocity=1*Controller.SCALE;
        }

        /**
         * konstruktor ustawia dane tak ja mu sie je poda i ustala stan poczatkowy
         * @param x wspolrzedna x
         * @param y wspolrzedna y
         * @param ll limit x z lewej strony
         * @param lr limit x z prawej strony
         */
        Thiccboy(int x, int y, int ll, int lr)
        {
            super(x, y, ll ,lr);
            magazine=0;
            rockets=new Missile[6];
            direction=true;
            spritenum=4;
            health=10;
        }

        /**
         * zasadnicza funkcja ruchu
         * @param i dlugosc kroku
         */
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

        /**
         * zasadnicza funkcja strzalu, wystrzeliwuje 4 pociski w dol i 2 po skosie w boki
         */
        public void shoot()
        {
            if(magazine>0) magazine=0;
            if(shot>0) return;
            rockets[0]=new Missile(crdx+(float)radius/4, crdy+radius, true);
            rockets[1]=new Missile(crdx-(float)radius/4, crdy+radius, true);
            rockets[2]=new Missile(crdx-(float)radius/4, crdy, true);
            rockets[3]=new Missile(crdx+(float)radius/4, crdy, true);
            rockets[4]=new Diagmissile(crdx+(float)radius/4, crdy, true, 2, direction);
            rockets[5]=new Diagmissile(crdx-(float)radius/4, crdy, true, 2, !direction);
            shot+=6;
        }

        /**
         * reset pociskow
         */
        public void reload() {shot=0; rockets=new Missile[6]; magazine=0;}

        /**
         * getter promienia
         * @return promien
         */
        public int getr(){return radius;}

        /**
         * getter punktow
         * @return liczba punktow
         */
        public double getscore(){return 10*weight;}
    }

    /**
     * Hivewitch porusza sie jak RUnner, nie rusz sie w dol, ma wiecej zycia i
     * zamiast strzalu przyzywa dodatkowe Smallfry do walki. Posiada takze zmienna
     * magazine, ale takze mandate - mandate zabrania jej przyzywac przeciwnikow
     * za czesto.
     */
    private static class Hivewitch extends Attacker
    {
        /**
         * promien
         */
        static final int radius;
        /**
         * zmienna orientacyjna generatora i zasadnicza punktow
         */
        static final double weight;
        private static final double velocity;
        private int magazine, mandate;
        static
        {
            radius=(int)(40*Controller.SCALE);
            weight=2;
            velocity=1*Controller.SCALE;
        }

        /**
         * konstruktor ustawia zmienne tak jak mu sie poda i ustala stan poczatkowy.
         * @param x wspolrzedna x
         * @param y wspolrzedna y
         * @param ll limit x z lewej
         * @param lr limit x z prawej
         */
        Hivewitch(int x, int y, int ll, int lr)
        {
            super(x, y, ll, lr);
            rockets=null;
            direction=true;
            spritenum=5;
            health=10;
            mandate=200;
        }

        /**
         * zasadnicza funkcja ruchu
         * @param i dlugosc kroku
         */
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

        /**
         * funkcja strzalu - dodaje do queue 2 Smallfry
         */
        public void shoot()
        {
            if(mandate>0) return;
            mandate=200;
            magazine=0;
            queue.add(new SmallFry((int)crdx+radius*2, (int)crdy+radius*2, (0>crdx-SmallFry.radius*10)?0:(int) crdx-SmallFry.radius*10, (Controller.SIZE*2<crdx+SmallFry.radius*10)?Controller.SIZE*2:(int)crdx+SmallFry.radius*10));
            queue.add(new SmallFry((int)crdx-radius*2, (int)crdy+radius*2, (0>crdx-SmallFry.radius*10)?0:(int)crdx-SmallFry.radius*10, (Controller.SIZE*2<crdx+SmallFry.radius*10)?Controller.SIZE*2:(int)crdx+SmallFry.radius*10));
        }

        /**
         * nic
         */
        public void reload() {}

        /**
         * getter promienia
         * @return promien
         */
        public int getr(){return radius;}

        /**
         * getter punktow
         * @return liczba punktow
         */
        public double getscore(){return 10*weight;}
    }

    /**
     * porusza sie jak Runner - nie idzie w dol. Strzela laserem, jest duzy i ma duzo zycia.
     * Posiada magazine, ale takze counter - timer animacji.
     */
    private static class Laserboy extends Attacker
    {
        /**
         * promien
         */
        static final int radius;
        /**
         * zmienna orientacyjna generatora i zasadnicza punktow
         */
        static final double weight;
        private static final double velocity;
        private int magazine, counter;
        static
        {
            radius=(int)(30*Controller.SCALE);
            weight=0.75;
            velocity=1*Controller.SCALE;
        }

        /**
         * konstruktor ustawia dane tak jak mu sie poda i ustala stan poczatkowy
         * @param x wspolrzedna x
         * @param y wspolrzenda y
         * @param ll limit x z lewej
         * @param lr limit x z prawej
         * @param b kierunek ruchu
         */
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

        /**
         * zasdnicza funkcja ruchu, zawiera sie w niej rowniez automat sterujacy animacja.
         * @param i dlugosc kroku
         */
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

        /**
         * funkcja pomocna automatu strzalu
         */
        public void shoot()
        {
            magazine=0;
            if(spritenum==6)
            {
                spritenum=-6;
            }
        }

        /**
         * zwraca pocisk z lsity na indeksie k, w tym przypadku sa 3 pociski na samym dole laseru
         * @param k indeks na liscie
         * @return nowy pocisk wygenerowany na potrzeby laseru
         */
        public Missile extract(int k)
        {
            if(spritenum!=-66) return null;
            return new Missile(crdx+(k-1)*(float)radius/2, Ship.HEIGHT, true);
        }

        /**
         * reset laseru
         */
        public void reload() {magazine=0; counter=0; spritenum=6; shot=0;}

        /**
         * getter promienia
         * @return promien
         */
        public int getr(){return radius;}

        /**
         * getter punktow
         * @return liczba punktow
         */
        public double getscore(){return 10*weight;}
    }

    /**
     * Lamp porusza sie po kole, ma wokol siebie takze krazace, strzelajace Moth'y.
     * Ma dodatkowo zmienna orbit - promien suchu, angle - pozycje na kole, startx,
     * starty - wspolrzedne srodka kola, oraz magazine. Nie moze strzelac, ale moze
     * wskrzeszac swoje Moth'y.
     */
    private static class Lamp extends Attacker
    {
        /**
         * promien
         */
        static final int radius;
        /**
         * zmienna orientacyjna generatora i zasadnicza punktow
         */
        static final double weight;
        private static final double velocity;
        private static final int orbit;
        private Moth[] minions=new Moth[3];
        private int magazine, startx, starty;
        private double angle;
        static
        {
            radius=(int)(30*Controller.SCALE);
            weight=2;
            velocity=1*Controller.SCALE;
            orbit=(int) (175*Controller.SCALE);
        }

        /**
         * konstruktor ustawia dane tak jak sie je poda, i ustala stan poczatkowy
         * @param x wsporlzedna x srodka kola
         * @param y wspolrzedna y srodka kola
         * @param b kierunek ruchu
         * @param a pozycja w radianach na kole
         */
        Lamp(int x, int y, boolean b, double a)
        {
            magazine=0;
            direction=b;
            spritenum=7;
            crdx=x+(orbit)*Math.cos(a);
            startx=x;
            crdy=y+(orbit)*Math.sin(a);
            starty=y;
            health=5;
            angle=a;
            for(int i=0; i<3; i++)
            {
                minions[i]=new Moth((int) crdx, (int) crdy, (150+i*120)*Math.PI/180, !direction, Lamp.radius+Moth.radius);
                list.add(minions[i]);
            }
        }

        /**
         * zasadnicza funkcja ruchu po kole
         * @param i dlugosc kroku
         */
        public void movement(int i)
        {
            angle+=(direction)?i*velocity*Math.PI/180:-i*velocity*Math.PI/180;
            crdx=startx+orbit*Math.cos(angle);
            crdy=starty+orbit*Math.sin(angle);
            for(Moth m:minions)
            {
                m.movement(crdx, crdy);
                if(m.isgone()) m.movement(i);
            }
            magazine+=i;
            if(magazine>=100) shoot();
        }

        /**
         * funkcja strzalu, ewentualnie wskrzesza upadle Moth'y
         */
        public void shoot()
        {
            magazine=0;
            for(Moth m: minions)
            {
                if(m.isgone()) m.res();
                m.shoot();
            }
        }

        /**
         * nic
         */
        public void reload() {}

        /**
         * getter promienia
         * @return promien
         */
        public int getr(){return radius;}

        /**
         * getter punktow
         * @return liczba punktow
         */
        public double getscore(){return 10*weight;}
    }

    /**
     * Moth porusza sie po okregu podobnie jak Lamp, ale moze strzelac, ma
     * 2 rakiety oraz ma dodatkowo opcje zmiany srodka okregu po ktorym sie
     * porusza. Moze takze zostac wskrzeszona.
     */
    private static class Moth extends Attacker
    {
        /**
         * zmienna orientacyjna generatora i zasadnicza punktow
         */
        static final double weight;
        /**
         * promien
         */
        static final int radius;
        private static final double velocity;
        private double angle;
        private int startx, starty, orbit;
        static
        {
            radius=(int)(16*Controller.SCALE);
            weight=0;
            velocity=1*Controller.SCALE;
        }

        /**
         * konstruktor ustawia dane jak mu sie poda i ustala stan poczatkowy
         * @param x wspolrzedna x srodka okregu
         * @param y wspolrzedna y srodka okregu
         * @param a pozycja w kacie w radianach na okregu
         * @param b kierunek ruchu
         * @param o dlugosc orbity
         */
        Moth(int x, int y, double a, boolean b, int o)
        {
            rockets=new Missile[2];
            direction=b;
            spritenum=8;
            orbit=o;
            crdx=x+orbit*Math.cos(a);
            startx=x;
            crdy=y+orbit*Math.sin(a);
            shot=0;
            starty=y;
            health=1;
            angle=a;
        }

        /**
         * zasadnicza funkcja ruchu po okregu
         * @param i dlugosc kroku
         */
        public void movement(int i)
        {
            angle+=(direction)?i*velocity*Math.PI/180:-i*velocity*Math.PI/180;
            crdx=startx+orbit*Math.cos(angle);
            crdy=starty+orbit*Math.sin(angle);
            if(!isgone()) moverocket(i);
        }

        /**
         * wskrzeszanie, dodaje sie do queue
         */
        void res() {health=1;queue.add(this); reload();}

        /**
         * funkcja zmiany srodka okregu po ktorym sie porusza
         * @param x wspolrzedna x
         * @param y wspolrzedna y
         */
        void movement(double x, double y)
        {
            startx+=x-startx;
            starty+=y-starty;
        }

        /**
         * reset pociskow
         */
        public void reload() {shot=0; rockets=new Missile[2];}

        /**
         * getter promienia
         * @return promien
         */
        public int getr(){return radius;}

        /**
         * getter punktow
         * @return liczba punktow
         */
        public double getscore(){return 10*weight;}
    }

    /**
     * Glitch teleportuje sie losowo po planszy i zawsze celuje w gracza. Ma zmienna
     * magazine i counter i 3 rakiety. Counter sluzy do generacji liczb pseudolosowych.
     */
    private static class Glitch extends Attacker
    {
        /**
         * promien
         */
        static final int radius;
        /**
         * zmienna orientacyjna generatora i zasadnicza punktow
         */
        static final double weight;
        private int magazine;
        private double counter;

        /**
         * wlasny generator liczb pseudolosowych
         * @return liczba pseudoloswa
         */
        private int evolve() {counter=(counter*seed[0]*947*109/107+counter*seed[0]*947*109/107)%(5*Controller.SIZE); return (int) counter;}
        static
        {
            radius=(int)(26*Controller.SCALE);
            weight=0.25;
        }

        /**
         * konstruktor ustawia dane tka jak je sie da, i ustala stan poczatkowy
         * @param x wspolrzedna x
         * @param y wspolrzedna y
         * @param ctr stan poczatko0wy generatora liczb pseudolosowych
         */
        Glitch(int x, int y, double ctr)
        {
            super(x, y, 0, 0);
            rockets=new Missile[3];
            spritenum=9;
            health=3;
            counter=ctr;
            magazine=0;
        }

        /**
         * zasadnicza funkcja teleportacji i zmian w modelu
         * @param i dlugosc kroku
         */
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
                crdy=evolve()%(Controller.SIZE*Controller.RATIO-300)+radius;
                shoot();
                magazine=0;
            }
            moverocket(i);
        }

        /**
         * zasadnicza funkcja strzalu, celuje w gracza
         */
        public void shoot()
        {
            if(magazine<=250) magazine=0;
            if(shot>=rockets.length) return;
            rockets[shot]=new Diagmissile(crdx, crdy, true, Math.abs(((double)Ship.HEIGHT-crdy)/((double)ship.getx()-crdx)), ship.getx()>crdx);
            shot++;
        }

        /**
         * reset pociskow
         */
        public void reload() {shot=0; rockets=new Missile[3];}

        /**
         * getter promienia
         * @return promien
         */
        public int getr(){return radius;}

        /**
         * getter punktow
         * @return liczba punktow
         */
        public double getscore(){return 10*weight;}
    }

    /**
     * Gargantua jest bossem, porusz sie po przyblizeniu trojkata, prawie caly czas strzela i ma 3
     * rozne specjalne ataki. Posiada rowniez wlasny generator liczb pseudolosowych, oraz enumeracje
     * stanu ktora mowi jaki atak obecnie jest wykonywany. Zmienna center pomaga w ruchu po trojkacie.
     * Z powodu duzej ilosci pociskow jakie gargantua posiada, zmienna rockets jest tu lista.
     */
    private static class Gargantua extends Attacker
    {
        /**
         * promien
         */
        static final int radius;
        /**
         * zasadnicza zmienna punktow
         */
        static final double weight;
        private enum Attack {hive, laser, barrage, blank}
        Attack attack;
        private Vector<Missile> rockets = new Vector<>();
        private static double velocity;
        private double random;
        private int magazine, center, counter, countdown, minions;

        /**
         * wlasny generator liczb pseudolosowych
         * @return liczba pseudolosowa
         */
        private int evolve() {random=(random*seed[0]*947*109/107+random*seed[0]*947*109/107)%5000; return (int) random;}
        static
        {
            radius=(int)(100*Controller.SCALE);
            weight=100;
            velocity=2*Controller.SCALE;
        }

        /**
         * konstruktor ustawia zmienne jak mu sie poda i ustala stan poczatkowy
         * @param x wspolrzedna x
         * @param y wspolrzedna y
         * @param ll limit z lewej
         * @param ctr stan pcozatkowy generatora liczb pseudolosowych
         */
        Gargantua(int x, int y, int ll, double ctr)
        {
            super(x, y, ll, x-ll+radius);
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

        /**
         * 1. atak specjalny - gargantua wypuszcza 10 Smallfry na plansze przez 5 sekund.
         * @param i czas jaki uplynal globalnie
         */
        private void hive(int i)
        {
            counter+=i;
            velocity=0;
            if(counter>=50)
            {
                queue.add(new SmallFry((int) crdx, (int) crdy+radius+SmallFry.radius, (int)crdx-10*SmallFry.radius, (int) crdx+10*SmallFry.radius));
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

        /**
         * 2. atak specjalny - gargantua wystrzeliwuje kilkadziesiat pociskow w losowych
         * kierunkach przez 5 sekund.
         * @param i czas jaki uplynal globalnie
         */
        private void barrage(int i)
        {
            velocity=0;
            if(evolve()%100>=85)
            {
                rockets.add(new Diagmissile(crdx+evolve()%(radius*2+1)-radius, crdy, true, 5000/(double) evolve(),evolve()%2==1));
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

        /**
         * 3. atak specjalny - gargantua strzela przed siebie laserem przez 2.5 sekundy.
         * @param i czas jaki uplynal globalnie
         */
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

        /**
         * funkcja strzalu ciaglego, zawieszana podczas lasera
         */
        public void shoot()
        {
            magazine=0;
            rockets.add(new Missile(crdx, crdy, true));
            shot++;
        }

        /**
         * funkcja wyciagajaca pocisk z pozycji na liscie
         * @param k indeks na liscie
         * @return pocisk z pozycji k, badz nowy pocisk lasera
         */
        public Missile extract(int k)
        {
            if(shot==0||(k>=shot&&attack!=Attack.laser)) return null;
            if(k<rockets.size()) return rockets.get(k);
            return new Missile(crdx-radius+(k-rockets.size())*((float)radius/7)+((float)k/3)*((float)radius/7), Ship.HEIGHT, true);
        }

        /**
         * funkcja pomocnicza ruchu
         * @param i dlugosc kroku
         */
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
                }
            }
        }

        /**
         * reset pociskow
         */
        public void reload() {shot=0; rockets.clear(); magazine=0; counter=0; attack=Attack.blank; countdown=0; velocity=2; spritenum=10;}

        /**
         * getter prmienia
         * @return promien
         */
        public int getr(){return radius;}

        /**
         * getter punktow
         * @return liczba punktow
         */
        public double getscore(){return 10*weight;}
    }
}
