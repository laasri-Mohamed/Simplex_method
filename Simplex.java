public class Simplex {
    private int rows, cols; 
    private float[][] table; 
    private boolean solutionIsUnbounded = false;
    // énumirations 
    public static enum ERROR{
        NOT_OPTIMAL,
        IS_OPTIMAL,
        UNBOUNDED;
    }
    
    public Simplex(int numDesConstraints, int numinconnues){
        rows = numDesConstraints+1; 
        cols = numinconnues+1;   
        table = new float[rows][cols]; 
        
        // initialize references to arrays
        for(int i = 0; i < rows; i++){
            table[i] = new float[cols];
        }
    }
    
    // prints out the simplex tableau
    public void print(){
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
                String value = String.format("%.2f", table[i][j]);
                System.out.print(value + "\t");
            }
            System.out.println();
        }
        System.out.println();
    }
    
    // fills the simplex table with coefficients
    public void fillTable(float[][] data){
        for(int i = 0; i < table.length; i++){
            System.arraycopy(data[i], 0, this.table[i], 0, data[i].length);
        }
    }
    
    // computes the values of the simplex table
    // should be used in a loop to continuously compute until an optimal solution is found
    public ERROR compute(){
        // Step 1
        if(checkOptimality()){
            return ERROR.IS_OPTIMAL; // solution is optimal
        }
        
        // step 2
        // find the entering column
        int pivotColumn = findEnteringColumn();
        System.out.println("Pivot Column: "+pivotColumn);
        
        // step 3
        // find departing value
        float[] ratios = calculateRatios(pivotColumn);
        if(solutionIsUnbounded == true)
            return ERROR.UNBOUNDED;
        int pivotRow = findSmallestValue(ratios);
        //System.out.println("Pivot row: "+ pivotRow);
        
        // step 4
        // form the next table
        formNextTable(pivotRow, pivotColumn);
        
        // since we formed a new table so return NOT_OPTIMAL
        return ERROR.NOT_OPTIMAL;
    }
    
    // Forms a new table from precomputed values.
    private void formNextTable(int pivotRow, int pivotColumn){
        float pivotValue = table[pivotRow][pivotColumn];
        float[] pivotRowVals = new float[cols];
        float[] pivotColumnVals = new float[cols];
        float[] rowNew = new float[cols];
        
        // divide all entries in pivot row by entry in pivot column
        // get entry in pivot row
        System.arraycopy(table[pivotRow], 0, pivotRowVals, 0, cols);
        
        // get entry in pivot column
        for(int i = 0; i < rows; i++)
            pivotColumnVals[i] = table[i][pivotColumn];
        
        // divide values in pivot row by pivot value
        for(int  i = 0; i < cols; i++)
            rowNew[i] =  pivotRowVals[i] / pivotValue;
        
        // subtract from each of the other rows
        for(int i = 0; i < rows; i++){
            if(i != pivotRow){
                for(int j = 0; j < cols; j++){
                    float c = pivotColumnVals[i];
                    table[i][j] = table[i][j] - (c * rowNew[j]);
                }
            }
        }
        
        // replace the row
        System.arraycopy(rowNew, 0, table[pivotRow], 0, rowNew.length);
    }
    
    // calculates the pivot row ratios
    private float[] calculateRatios(int column){
        float[] positiveEntries = new float[rows];
        float[] res = new float[rows];
        int allNegativeCount = 0;
        for(int i = 0; i < rows; i++){
            if(table[i][column] > 0){
                positiveEntries[i] = table[i][column];
            }
            else{
                positiveEntries[i] = 0;// if a value in the pivot column is negative, its new value becomes 0 
                allNegativeCount++;// counts the number of negative entries
            }
            //System.out.println(positiveEntries[i]);
        }
        
        if(allNegativeCount == rows) // if all the entries were negative
            this.solutionIsUnbounded = true;
        else{
            for(int i = 0;  i < rows; i++){
                float val = positiveEntries[i];
                if(val > 0){
                    res[i] = table[i][cols -1] / val;// counts the different ratios
                }
            }
        }
        
        return res;
    }
    
    // finds the next entering column
    private int findEnteringColumn(){
        float[] values = new float[cols];
        int location = 0;//column index 
        
        int pos, count = 0;// pos:all the columns except for the solution column
        for(pos = 0; pos < cols-1; pos++){
            if(table[rows-1][pos] < 0){
                //System.out.println("negative value found");
                count++;
            }
        }
        
        if(count > 1){
            for(int i = 0; i < cols-1; i++)
                values[i] = Math.abs(table[rows-1][i]);
            location = findLargestValue(values);
        } else location = count - 1;
        
        return location;
    }
    
    
    // finds the smallest value in an array
    private int findSmallestValue(float[] data){
        float minimum ;
        int c, location = 0;
        minimum = data[0];
        
        for(c = 1; c < data.length; c++){
            if(data[c] > 0){
                if(Float.compare(data[c], minimum) < 0){
                    minimum = data[c];
                    location  = c;
                }
            }
        }
        
        return location;
    }
    
    // finds the largest value in an array
    private int findLargestValue(float[] data){
        float maximum = 0;
        int c, location = 0;
        maximum = data[0];
        
        for(c = 1; c < data.length; c++){
            if(Float.compare(data[c], maximum) > 0){
                maximum = data[c];
                location  = c;
            }
        }
        
        return location;
    }
    
    // checks if the table is optimal
    public boolean checkOptimality(){
        boolean isOptimal = false;
        int pCount = 0;// count the number of the positive values
        
        for(int i = 0; i < cols-1; i++){
            float val = table[rows-1][i];
            if(val >= 0){
                pCount++;
            }
        }
        
        if(pCount == cols-1){
            isOptimal = true;
        }
        
        return isOptimal;
    }

    // returns the simplex tableau
    public float[][] getTable() {
        return table;
    }



public static void main(String[] args) {
    
    boolean quit = false;
    
    System.out.println("\t 8888888888',8888'{1}8 8888888888   {0}8 888888888o. {1}     ,o888888o.");
    System.out.println("\t        ,8',8888' {1}8 8888         {0}8 8888    `88.{1}  . 8888     `88.");
    System.out.println("\t       ,8',8888'  {1}8 8888         {0}8 8888     `88{1} ,8 8888       `8b");
    System.out.println("\t      ,8',8888'   {1}8 8888         {0}8 8888     ,88{1} 88 8888        `8b");
    System.out.println("\t     ,8',8888'    {1}8 888888888888 {0}8 8888.   ,88'{1} 88 8888         88");
    System.out.println("\t    ,8',8888'     {1}8 8888         {0}8 888888888P' {1} 88 8888         88 ");
    System.out.println("\t   ,8',8888'      {1}8 8888         {0}8 8888`8b     {1} 88 8888        ,8P made with <3 by : Laasri Mohamed");
    System.out.println("\t  ,8',8888'       {1}8 8888         {0}8 8888 `8b.   {1} `8 8888       ,8P");
    System.out.println("\t ,8',8888'        {1}8 8888         {0}8 8888   `8b. {1}  ` 8888     ,88'");
    System.out.println("\t,8',8888888888888 {1}8 888888888888 {0}8 8888     `88.{1}    `8888888P'");
    
    
    //drop your values here
    float[][] FormeStandard =  {
            { 1,   71,   53,   1,   0, 0,  4},
            { 71,   33,  6,   0,   1, 0,  7},
            {55 , 5 , 9,  0,  0,   1,  8},
            {-3,  9,  -79,   0,  0,   0, 0}
    };

    Simplex simplex = new Simplex(3, 6);
    simplex.fillTable(FormeStandard);
    simplex.print();
    // if table is not optimal re-iterate
    while(!quit){
        Simplex.ERROR err = simplex.compute();
        if(err == Simplex.ERROR.NOT_OPTIMAL){
            System.out.println("--- Next Set ---");
            simplex.print();
                }
        else if(err == Simplex.ERROR.IS_OPTIMAL){
            simplex.print();
            quit = true;
        }
        else if(err == Simplex.ERROR.UNBOUNDED){
            System.out.println("---Solution is unbounded---");
            quit = true;
        }
    }
} 
}
