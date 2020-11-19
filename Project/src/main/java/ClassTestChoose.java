import com.ibm.wala.ipa.callgraph.CGNode;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * 类级的测试用例选择
 * @author great_fish
 */
public class ClassTestChoose implements TestChoose {
    @Override
    public void chooseTest(String changeInfoPath, DependencyGraph dependencyGraph) {
        ArrayList<String> changeInfoArray = Utils.readFile(changeInfoPath);

        Set<String> callers = new HashSet<String>(); //记录所有受影响的相关类名
        for (int i = 0; i < changeInfoArray.size(); ++i) {
            String[] singleChangeInfo = changeInfoArray.get(i).split(" ");
            String className = singleChangeInfo[0];
            // System.out.println(className);
            callers.addAll(dependencyGraph.getCallers(className));//找到并添加所有调用了className类的类(直接&间接)
        }

        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("selection-class.txt"));
            System.out.println("开始输出class级测试用例选择");
            for (String callerClass : callers) {//遍历每一个调用类
                //  System.out.println(callerClass+" "+callerClass.indexOf("test"));
                Set<String> methods = ((ClassDependencyGraph) dependencyGraph).getMethosOfClass(callerClass);//相关类下的所有方法
                for (String method : methods) {
                    CGNode cgNode = dependencyGraph.getNodeOfMethod(method);
                    //排除非测试方法
                    if (cgNode.getMethod().getAnnotations().toString().contains("Annotation type <Application,Lorg/junit/Test>")) {
                        System.out.println(method);
                        out.write(cgNode.getMethod().getDeclaringClass().getName().toString() + " " + method + "\n");
                    }
                }
            }
            out.close();
            System.out.println("输出class级测试用例选择结束");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
