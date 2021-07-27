import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author: 何胜豪
 * @Title: TODO
 * @Package: PACKAGE_NAME
 * @Description:
 * @date : 2021/7/27 23:19
 */
public class Test {
    public static void main(String[] args) {
        String url = "./image";
        File file = new File(url);
        if(!file.exists()){
            file.mkdir();
        }
        file = new File("./image/ss.png");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(new byte[]{1,2,3,4});
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
