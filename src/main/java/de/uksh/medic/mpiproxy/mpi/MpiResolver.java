package de.uksh.medic.mpiproxy.mpi;

public final class MpiResolver {
    
    private MpiResolver() {
    }

    public static String resolve(String patientId) {
        return "MPI_" + patientId;
    }

    public static String reverse(String mpiId) {
        return mpiId.replace("MPI_", "");
    }

}
