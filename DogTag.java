public class DogTag extends PowerUp{
    public DogTag(GamePanel p, Car bat, int x, int y) {
        panel = p;
        backgroundColour = panel.getBackground ();
  
        width = 25;
        height = 25;
  
        this.x = x;
        this.y = y;
  
        this.bat = bat;
        
        soundManager = SoundManager.getInstance();
        alienImage = ImageManager.loadImage ("images/dogTag.png");
    }

    public void move() {
        boolean CarCollision = collidesWithCar();
  
        if (!panel.isVisible ()) return;
            
        if (CarCollision) {
           soundManager.playClip("pickup", false);
           if(panel.getLifeTotal()>= 5){
              panel.addPoints(5);
           }else{
              panel.addLife(2);
           }
  
           panel.removeHealth();
        }
  
    }
}
