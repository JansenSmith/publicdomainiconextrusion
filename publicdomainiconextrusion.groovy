import eu.mihosoft.vrl.v3d.*
import eu.mihosoft.vrl.v3d.svg.SVGLoad


def depth
if(args==null){
	depth = 0.4
	println "No parameters found. Using depth = "+name
	print_fonts = true
} else {
	depth = args//.get(0)
}

// import sig SVG
File f = ScriptingEngine
	.fileFromGit(
		"https://github.com/JansenSmith/publicdomainiconextrusion.git",//git repo URL
		"main",//branch
		"public-domain-icon.svg"// File from within the Git repo
	)

	
//println "Extruding SVG "+f.getAbsolutePath()
SVGLoad s = new SVGLoad(f.toURI())
//println "Layers= "+s.getLayers()
// A map of layers to polygons
//HashMap<String,List<Polygon>> polygonsByLayer = s.toPolygons()
// extrude all layers to a map to 10mm thick
//HashMap<String,ArrayList<CSG>> csgByLayers = s.extrudeLayers(10)
// extrude just one layer to 10mm
// The string "1-holes" represents the layer name in Inkscape
def insideParts = s.extrudeLayerToCSG(depth,"insides")
// seperate holes and outsides using layers to differentiate
// The string "2-outsides" represents the layer name in Inkscape
def outsideParts = s.extrudeLayerToCSG(depth,"outside")

CSG sig = outsideParts.difference(insideParts).moveToCenter()

//sig = sig.rotz(45).movey(3).movex(-0.75)

sig = sig.toYMin().toXMax()
sig = sig.movex(-8).movey(12)
//sig = sig.mirrorx()

//println sig.totalZ

sig = sig.setColor(javafx.scene.paint.Color.DEEPPINK)
			.setName("sig")
			.addAssemblyStep(0, new Transform())
			.setManufacturing({ toMfg ->
				return toMfg
						//.rotx(180)// fix the orientation
						//.toZMin()//move it down to the flat surface
			})

return sig