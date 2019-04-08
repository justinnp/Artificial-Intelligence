//Justin Powell
//Assignment 4 - Multi Class Perceptron
//CAP4630
//Spring 2019
//UCF

//Notes and Requirements:
//command line args: 1) String: data file name 2) int: epochs
//bias with constant input value of 1 - assign last weight in vector for the bias
//inital weight vals should be 0.0

//Methods:
//buildClassifier() - train the classifier for epoch input or until no weight updates...
//... must report intermediate results - Epcho N [weight updates] successful=1 or unsuccessful=0
//distributionForInstance() 
//toString()
//empty concrete implementations for getCapabilities() and classifyInstance()

//package name
package multiclass;

//util dependencies
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
//weka dependencies
import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.Instance;
import weka.core.Instances;


public class MulticlassPerceptron implements weka.classifiers.Classifier {

    //will be 1st argument
    private String fileName;
    //will be 2nd argument
    private int epochs;
    private int trainingEpochs;
    private double[][] weights;
    private int bias;
    private int updates;

    //constructor and main function parsing our args
    public MulticlassPerceptron (String[] options){
        this.fileName = options[0];
        this.epochs = Integer.parseInt(options[1]);
        this.bias = 1;
        this.updates = 0;
        this.trainingEpochs = 0;
        printIntro();
    }

    public void buildClassifier(Instances instances){
        int attributes = instances.numAttributes();
        int classes = instances.numClasses();
        
        //initialize the weight matrix
        //initial weight vals are 0.0
        this.weights = new double[classes][attributes];
        for(int i = 0; i < classes; i++){
            Arrays.fill(this.weights[i], 0.0);
        }

        System.out.println();
        for(int i = 0; i < this.epochs; i++){
            int e = i + 1;
            System.out.print("Epoch\t\t" + e + " ");
            int count = 0;
            boolean epoch_change = false;
            StringBuilder sb = new StringBuilder();
            for(Instance instance : instances){
                int classification = (int) instance.value(instance.numAttributes() - 1);
                //prediction of current instance
                int prediction = predict(instance);
                //if successful classification, 1
                if(prediction == classification) sb.append("1"); 
                //if unsuccessful
                else{
                    //incorrect so we are going to update our weights
                    this.updates++;
                    count++;
                    epoch_change = true;
                    //0 = unsuccessful
                    sb.append("0");
                    for(int j = 0; j < attributes; j++){
                        if(j + 1 == attributes){
                            //increment based on actual classification, add bias
                            this.weights[classification][j] += this.bias;
                            //decriment based on our prediction, subtract bias
                            this.weights[prediction][j] -= this.bias;
                            break;
                        }
                        //increment based on actual classification, add instance value
                        this.weights[classification][j] += instance.value(j);
                        //decriment based on our prediction, subtract instance value
                        this.weights[prediction][j] -= instance.value(j);
                    }
                }
            }
            System.out.print("[ " + count + "] ");
            System.out.println(sb.toString());
            this.trainingEpochs++;
            //if no changes between epochs
            if(!epoch_change) break;
        }
        System.out.println();
    }

    //predict the instance classification
    public int predict(Instance instance){
        int attributes = instance.numAttributes();
        int classes = instance.numClasses();
        double[] attrs = new double[attributes];
        double[] dot_products = new double[classes];

        //grab dot product of weight vector and instance values vector
        for(int i = 0; i < instance.numClasses(); i++){
            double total = 0.0;
            for(int j = 0; j < instance.numAttributes(); j++){
                double dot = this.weights[i][j];
                if(j + 1 != instance.numAttributes()){
                    dot *= instance.value(j);
                }
                total += dot;
            }
            dot_products[i] = total;
        }

        //find maximum of our dot products -> correct classification
        int max_index = 0;
        for(int i = 1; i < dot_products.length; i++){
            if(dot_products[i] > dot_products[max_index]){
                max_index = i;
            } 
        }
        return max_index;
    }

    //empty concrete implementation of getCapability
    public Capabilities getCapabilities(){
        return null;
    }

    //empty concrete implementations of classifyInstance
    public double classifyInstance(Instance instance){
        return 0;
    }

    @Override
    public double[] distributionForInstance(Instance instance){
        double[] result = new double[instance.numClasses()];
        result[predict(instance)] = 1;
        return result;
    }


    //Print statements
    public void printIntro(){
        System.out.println();
        System.out.println("University of Central Florida");
        System.out.println("CAP4630 Artificial Intelligence - Spring 2019");
        System.out.println("Multi-Class Perceptron Classifier");
        System.out.println("Author(s): Justin Powell");
        System.out.println();
    }

    public String toString(){
        System.out.println("Source File:\t" + this.fileName);
        System.out.println("Training epoch limit:\t" + this.epochs);
        System.out.println("Actual training epochs:\t" + this.trainingEpochs);
        System.out.println("Total # weight updates:\t" + this.updates);
        System.out.println();
        System.out.println("Final weights:");
        System.out.println();

        //print classes, final weights
        for(int i = 0; i < weights.length; i++){
            System.out.print("Class " + i + " weights: ");
            for(int j = 0; j < this.weights[0].length; j++){
                System.out.printf("%.3f  ", this.weights[i][j]);
            }
            System.out.println();
        }
        return "";
    }

}
