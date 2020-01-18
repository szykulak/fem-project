
import org.la4j.linear.GaussianSolver;
import org.la4j.matrix.dense.Basic2DMatrix;
import org.la4j.vector.dense.BasicVector;
import java.io.FileNotFoundException;
import java.util.Arrays;

public class Simulation {


    public Simulation() {

    }

    public void heatTransferSimulation(Grid grid, MatrixCalculations matrixCalculations) throws FileNotFoundException { //todo tak przerobic zeby tu wczytywac wszystkie parametry z pliku?
        //grid.gridBuilder();
        double simulationTime = GlobalData.getSimulationTime();
        double simulationTimeStep = GlobalData.getSimulationStepTime();
        int initialTemperature = GlobalData.getInitialTemperature();

        int iterationsNumber = (int) simulationTime / (int) simulationTimeStep;
        double[] t0 = new double[GlobalData.getNumberOfNodes()];
        Arrays.fill(t0, initialTemperature);
        Element[] elements = grid.elementBuilder();

        matrixCalculations.xiDerivativesMatrix();
        matrixCalculations.shapeFunctionsMatrix();
        matrixCalculations.etaDerivativesMatrix();

        for (int i = 0; i < iterationsNumber; i++) {

            double[][] globalH = matrixCalculations.globalHMatrix(elements); //bc already included
            double[][] globalC = matrixCalculations.globalCMatrix(elements);
            double[] globalP = matrixCalculations.globalPVector(elements);
            for (int j = 0; j < globalP.length; j++) {
                globalP[j] *= -1;
            }

            for (int j = 0; j < globalH.length; j++) {
                for (int k = 0; k < globalH[j].length; k++) {
                    globalH[j][k] = globalH[j][k] + globalC[j][k] / simulationTimeStep;
                    globalP[k] = globalP[k] + (globalC[j][k] / simulationTimeStep * t0[j]);
                }
            }
      
            Basic2DMatrix HMatrix = new Basic2DMatrix(globalH); //h+c
            BasicVector PVector = new BasicVector(globalP);
            GaussianSolver equationSolver = new GaussianSolver(HMatrix);
            BasicVector t1 = (BasicVector) equationSolver.solve(PVector);
            t0 = t1.toArray();
            double []tmp=t1.toArray();
            System.out.println("h+c, iteration number: " + i);
            System.out.println(HMatrix);
            System.out.println("p+c, iteration number: " + i);
            System.out.println(PVector);
            System.out.println("temperatures: ");
            Arrays.sort(tmp);
            System.out.println(Arrays.toString(tmp));
            System.out.println("Temp. min: "+tmp[0]+", temp. max: "+tmp[tmp.length-1]);


        }


    }
}
