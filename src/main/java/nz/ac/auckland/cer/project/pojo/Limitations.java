package nz.ac.auckland.cer.project.pojo;

public class Limitations {

    private String cpuCores;
    private String memory;
    private String concurrency;

    public String getCpuCores() {

        return cpuCores;
    }

    public void setCpuCores(
            String cpuCores) {

        this.cpuCores = cpuCores;
    }

    public String getMemory() {

        return memory;
    }

    public void setMemory(
            String memory) {

        this.memory = memory;
    }

    public String getConcurrency() {

        return concurrency;
    }

    public void setConcurrency(
            String concurrency) {

        this.concurrency = concurrency;
    }

}