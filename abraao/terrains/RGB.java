package abraao.terrains;

public class RGB {
	  private double r, g, b;
	  public RGB (double r, double g, double b) {
	    this.r = r;
	    this.g = g;
	    this.b = b;
	  }
	  public RGB add (RGB rgb) {
	    return new RGB (r + rgb.r, g + rgb.g, b + rgb.b);
	  }
	  public RGB subtract (RGB rgb) {
	    return new RGB (r - rgb.r, g - rgb.g, b - rgb.b);
	  }
	  public RGB scale (double scale) {
	    return new RGB (r * scale, g * scale, b * scale);
	  }
	  private int toInt (double value) {
	    return (value < 0.0) ? 0 : (value > 1.0) ? 255 :
	      (int) (value * 255.0);
	  }
	  public int toRGB () {
	    return (0xff << 24) | (toInt (r) << 16) |
	      (toInt (g) << 8) | toInt (b);
	  }
	}