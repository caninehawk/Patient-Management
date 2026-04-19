import java.util.Arrays;

public class TestProject {

    public static int thirdLargest(int[] arr){
        Arrays.sort(arr);
        int count=2;
        int largest = arr[arr.length-1];
        for(int i=arr.length-1;i>=0;i--){
            if(arr[i]!=largest){
                count-=1;
            }
            if(count==0){
                return arr[i];
            }
        }
        return -1;
    }
    public static void main(String[] args) {
        int[] arr = { 20, 20, 50, 40, 88,99};
        System.out.println(thirdLargest(arr));
    }
}



an array with elements { 20, 20, 50, 40, 88,99}