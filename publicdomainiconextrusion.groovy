import eu.mihosoft.vrl.v3d.*
import eu.mihosoft.vrl.v3d.svg.SVGLoad
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


def depth
if(args==null){
	depth = 0.4
	println "No parameters found. Using depth = "+depth
	print_fonts = true
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


//println "Extruding SVG "+f.getAbsolutePath()
println "pubdom: isFxThread=" + javafx.application.Platform.isFxApplicationThread()
def parts = [null, null]
def latch = new java.util.concurrent.CountDownLatch(1)
javafx.application.Platform.runLater({
    try {
        println "pubdom runLater: starting SVGLoad"
        SVGLoad s = new SVGLoad(f.toURI())
        parts[0] = s.extrudeLayerToCSG(depth,"insides")
        parts[1] = s.extrudeLayerToCSG(depth,"outside")
        println "pubdom runLater: done, inside.totalZ=${parts[0].totalZ}, outside.totalZ=${parts[1].totalZ}"
    } catch(Exception e) {
        println "pubdom runLater error: " + e
    } finally {
        latch.countDown()
    }
})
boolean completed = latch.await(10, java.util.concurrent.TimeUnit.SECONDS)
println "pubdom: latch completed=${completed}, parts=${parts}"

CSG ret = parts[1].difference(parts[0]).moveToCenter()

//sig = sig.rotz(45).movey(3).movex(-0.75)

//sig = sig.toYMin().toXMax()
//sig = sig.movex(-8).movey(12)
//sig = sig.mirrorx()

//println sig.totalZ

ret = ret.setColor(javafx.scene.paint.Color.BLACK)
			.setName("pubdom")
			.addAssemblyStep(0, new Transform())
			.setManufacturing({ toMfg ->
				return toMfg
						//.rotx(180)// fix the orientation
						//.toZMin()//move it down to the flat surface
			})

return ret