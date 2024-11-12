import static java.lang.Math.*;
import java.util.Random;

public class LabFirst
{
    public static void main(String[] args)
    {
        // 1
        
        final short START_POINT = 6;
        final short END_POINT = 24;
        final short BORDER = 1;
        final short C_LENGTH = (END_POINT - START_POINT) / 2 + BORDER;
        int[] c = new int[C_LENGTH];

        for (int i = 0; i < C_LENGTH; i++)
            c[i] = (int)(END_POINT - 2 * i);

        // 2
        
        final short X_LENGTH = 11;
        double[] x = new double[X_LENGTH];

        Random random = new Random();
        final double RANDOM_MAX = 15.0;
        final double RANDOM_MIN = -5.0;

        for (int i = 0; i < X_LENGTH; i++)
            x[i] = random.nextDouble() * (RANDOM_MAX - RANDOM_MIN) + RANDOM_MIN;

        // 3
        
        double[][] array = new double[10][11];

        for (int i = 0; i < 10; i++)
        {
            for (int j = 0; j < 11; j++)
            {
                switch (c[i])
                {
                    case 20 -> array[i][j] = pow(2 / (sin(atan(E * ((x[j] + 5) / 2) + 1))), 2);
                    case 6, 8, 12, 16, 24 -> array[i][j] = cbrt(pow((atan(E * ((x[j] + 5) / 2) + 1)) * (tan(x[j]) + (1 / 4)), 3));
                    default -> array[i][j] = asin(pow(E, cbrt( (-1) * ( pow(cos(cos(log( abs(x[j]) ))), 2) ) )));
                }
            }
        }

        // 4
        
        for (int i = 0; i < 10; i++)
        {
            for (int j = 0; j < 11; j++)
            {
                System.out.printf("%13.5f ", array[i][j]);
            }
            System.out.println();
        }
    }
}
