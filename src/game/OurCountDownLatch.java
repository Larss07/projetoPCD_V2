package game;

public class OurCountDownLatch {
    private int contador;
    
    public OurCountDownLatch (int cont) {
        contador = cont;
    }
    
    public int getCount() {
        return contador;
    }

    public synchronized void countDown() {
        contador--;
        if (contador == 0) 
        notifyAll();
    }

    public synchronized void await() throws InterruptedException {
        while(contador > 0)
            wait();
    }
}
