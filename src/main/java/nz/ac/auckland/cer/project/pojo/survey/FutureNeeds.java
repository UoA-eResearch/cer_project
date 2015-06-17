package nz.ac.auckland.cer.project.pojo.survey;

public class FutureNeeds extends BaseCategory {

	public FutureNeeds() {
	    super.template = "__OPTIONS__";
	    super.optionStrings = new String[] {
	    	    "More CPU cores per cluster node to run larger multi-threaded jobs",
	    	    "More memory per compute node",
	    	    "More GPUs",
	    	    "More Intel Xeon Phi's",
	    	    "More disk space",
	    	    "Advice on how to parallelise/scale/tune my software",
	    	    "More/better general support",
	    	    "Fast interconnect between the machines for my MPI jobs",
	    	    "Shorter wait times"
	    };


	}
}
