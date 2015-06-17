package nz.ac.auckland.cer.project.pojo.survey;

public class PerfImpBigger extends BaseCategory {

	public PerfImpBigger() {
	    super.template = "I can run larger jobs now, up to __FACTOR__ times larger than before, thanks to: __OPTIONS__";
	    super.optionStrings = new String[] { 
	        "Distributed memory parallelisation",
	        "More memory available",
	        "More disk space",
	        "I don't know"
	    };	
	}
}
