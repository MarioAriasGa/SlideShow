package net.homelinux.mck.slideshow.core;

public class StopWatch {
	private long ini;
	private long end;
	
	public StopWatch() {
		reset();
	}
	
	public void reset() {
		ini = end = System.currentTimeMillis();
	}
	
	public void stop() {
		end = System.currentTimeMillis();
	}
	
	public long getMeasure() {
		return end-ini;
	}
	
	public long stopAndGet() {
		stop();
		return getMeasure();
	}
	
	public String stopAndShow() {
		stop();
		return toString();
	}
	
	@Override
	public String toString() {
		return msToString(getMeasure());
	}
	
    public static String msToString(long ms) {
        long totalSecs = ms/1000;
        long hours = (totalSecs / 3600);
        long mins = (totalSecs / 60) % 60;
        double secs = (totalSecs % 60) + (ms%1000)/1000.0;

        
        StringBuffer out= new StringBuffer();
        if(hours>0) {
        	out.append(hours).append(" hour ");
        }
        if(mins>0) {
        	out.append(mins).append(" min ");
        }
        out.append(secs).append(" sec");
        
        return out.toString();
    }

}
