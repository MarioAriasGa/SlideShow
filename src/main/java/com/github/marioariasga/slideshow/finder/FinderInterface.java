package net.homelinux.mck.slideshow.finder;

public interface FinderInterface {
	public void findAll(String path);
	
	public void randomize();

	public String get(int num);
    
    public int getSize();
    
    public void remove(int num);
    
    public void remove(String value);
    
    public void print();
}
