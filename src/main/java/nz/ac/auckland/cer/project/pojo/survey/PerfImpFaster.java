package nz.ac.auckland.cer.project.pojo.survey;


public class PerfImpFaster extends BaseCategory {

	public PerfImpFaster() {
	    super.template = "My jobs run __FACTOR__ times faster than before, thanks to: __OPTIONS__";
	    super.optionStrings = new String[] { 
	        "Algorithmic improvements to my software",
	        "Compile-time optimization of my software",
	        "Distributed memory parallelisation",
	        "Shared memory parallelisation",
	        "I don't know and need help figuring out why"
	    };
	}
}