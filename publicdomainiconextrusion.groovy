import eu.mihosoft.vrl.v3d.*
import eu.mihosoft.vrl.v3d.svg.SVGLoad


def depth
if(args==null){
	depth = 0.4
	println "No parameters found. Using depth = "+depth
} else {
	depth = args.get(0)
}

// import sig SVG
File f = ScriptingEngine
	.fileFromGit(
		"https://github.com/JansenSmith/publicdomainiconextrusion.git",//git repo URL
		"main",//branch
		"public-domain-icon.svg"// File from within the Git repo
	)

SVGLoad s = new SVGLoad(f.toURI())
def insideParts = s.extrudeLayerToCSG(depth,"insides")
// outer polygon has CW winding so SVGLoad extrudes it to depth+1; rescale to depth
def outsideParts = s.extrudeLayerToCSG(depth,"outside").toZMin().scaleToMeasurmentZ(depth)
println "pubdom: inside.totalZ=${insideParts.totalZ}, outside.totalZ=${outsideParts.totalZ}"

CSG ret = outsideParts.difference(insideParts).toXMin().toYMin().toZMin().movey(-2)

ret = ret.setColor(javafx.scene.paint.Color.BLACK)
			.setName("pubdom")
			.addAssemblyStep(0, new Transform())
			.setManufacturing({ toMfg ->
				return toMfg
						//.rotx(180)// fix the orientation
						//.toZMin()//move it down to the flat surface
			})

return ret
