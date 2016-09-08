package abraao.terrains;

import com.flowpowered.noise.model.Plane;

import abraao.MidpointDisplacement;
import net.jlibnoise.generator.Perlin;

public class GenerateMap {
	
//	public static void main(String[] args) {
//		  
//		int CX = 256;
//		int CY = 256;
//		int CZ = 256;
//		
//	  Perlin perlin = new Perlin();
//	  perlin.setFrequency(Perlin.DEFAULT_PERLIN_FREQUENCY);
//	  perlin.setPersistence(Perlin.DEFAULT_PERLIN_PERSISTENCE);
//	  perlin.setSeed(Perlin.DEFAULT_PERLIN_SEED);
//	  perlin.setNoiseQuality(NoiseQuality.BEST);
//	  
//	  for(int x = 0; x < CX; x++) {
//	    for(int z = 0; z < CZ; z++) {
//            for (int y = 0; y < CY; y++) {
//            	double height = perlin.getValue(x, y, z) * CY;
//            	System.out.println(height);
//            }				
//			
//	  	}
//	  }
//	}
	
	public static void main(String[] args) {
		
//		Perlin perlin = new Perlin();
//		System.out.println(perlin.getValue(2.1, 1.1, 1.1));
//		
		Plane plane = new Plane(new com.flowpowered.noise.module.source.Perlin());
//		
//		System.out.println(plane.getValue(1.1, 1.1));
		
		int CX = 256;
		int CY = 256;
		int CZ = 256;
		
	  Perlin perlin = new Perlin();
	  
	  for(int z = 0; z < CZ; z++) {
	  	for(int x = 0; x < CX; x++) {
	    	
	    	double nx = (double)x/(double)CX- 0.5, nz = (double)z/(double)CZ- 0.5;
	    	double height = perlin.getValue(nx, nz, 0);
            
//	    	height = height  / 2 + 0.5;
	    	
//	    	for (int y = 0; y <= height; y++) {
            	System.out.println(height);
//            }				
			
	  	}
	  }
	}	
	
//	public static void main(String[] args) {
//		MidpointDisplacement mid = new MidpointDisplacement();
//		
////		mid.hmult = 72;
////		mid.wmult = 72;
//		mid.n = 7;
//		
//		int[][] map = mid.getMap();
//		System.out.println(map.length + " " + map[0].length);
//	}
	
//	public static void main(String[] args) {
//		
//		int lod = 7;//level of detail
//		double roughness = .5;
//		
//		FractalTerrain frac = new FractalTerrain(lod , roughness);
//		
//		System.out.println(frac);
//	}
}
