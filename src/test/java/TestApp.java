import com.meetmitul.JsonConverter;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import java.io.IOException;

public class TestApp {

    public static void main(String[] args){
        try {
            JsonConverter jc = new JsonConverter();
            jc.execute();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MojoFailureException e) {
            e.printStackTrace();
        } catch (MojoExecutionException e) {
            e.printStackTrace();
        }


    }
}
