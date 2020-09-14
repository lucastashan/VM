public class GM {
    
  private final int NUM_FRAMES = 64; // 1024 
  private final int NUM_PAGES = 16; // 16 linhas

  private boolean[] framesLivres = new boolean[NUM_FRAMES];

  public GM(){
    this.framesLivres = initFrames();
  }

  public boolean[] getFrames(){
    return this.framesLivres;
  }

  public int pageStart(int page){
    return page * NUM_PAGES; 
  }

  public int pageEnd(int page){
    return ((page - 1) * NUM_PAGES) - 1;
  }

  public boolean[] initFrames(){
    boolean[] framesLivres = new boolean[NUM_FRAMES];
    for(int i=0; i < this.framesLivres.length; i++){
      framesLivres[i] = true;
    }
    return framesLivres;
  }

  public int countFramesLivres(){
    count = 0;
    for(int i=0; i < this.framesLivres.length; i++){
      if(framesLivres[i] == true){
        count += 1;
      }
    }
    return count;
  }

  public int[] aloca(int nroPalavras){
    int pages = nroPalavras / NUM_PAGES;
    int[] frames = new int[pages];
    if(pages > countFramesLivres()) return null;
    
    for(int i = 0; i < frames.length; i++){
      for(int j = i; j < framesLivres.length;j++){
        if( framesLivres[j] ) {
          frames[i] = j;
          framesLivres[i] = false;
        } 
      }
    }

    return frames;
  }

  public void desaloca(int[] pages){
    //Desaloca da memoria
      // TO DO
    //Liberando Frames
    for(int i=0; i < pages.length; i++){
      this.framesLivres[pages[i]] = true;
    }
  }

}
