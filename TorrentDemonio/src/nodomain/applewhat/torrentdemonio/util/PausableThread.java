package nodomain.applewhat.torrentdemonio.util;

public interface PausableThread {

	public abstract void start();

	public abstract void stop();

	public abstract void destroy();

}