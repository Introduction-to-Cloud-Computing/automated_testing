import com.ibm.wala.ipa.callgraph.CGNode;

import java.io.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import static com.ibm.wala.types.ClassLoaderReference.Application;

/**
 * 方法级的测试用例选择
 * @author great_fish
 */
public class MethodTestChoose implements TestChoose {
    @Override
    public void chooseTest(String changeInfoPath, DependencyGraph dependencyGraph) {
        ArrayList<String> changeInfoArray = Utils.readFile(changeInfoPath);//变更信息

        Set<String> callers = new HashSet<String>(); //记录所有受影响的相关方法名
        for (int i = 0; i < changeInfoArray.size(); ++i) {
            String[] singleChangeInfo = changeInfoArray.get(i).split(" ");
            String methodName = singleChangeInfo[1];
            // System.out.println(className);
            callers.addAll(dependencyGraph.getCallers(methodName));//找到并添加所有调用了该变更方法的所有方法(直接&间接)
        }
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter("selection-method.txt"));
            System.out.println("————————开始输出method级测试用例选择—————————");
            for (String callerMethod : callers) {//遍历每一个调用方法
                CGNode cgNode = (dependencyGraph).getNodeOfMethod(callerMethod);
                //排除非测试方法
                if (cgNode.getMethod().getAnnotations().toString().contains("Annotation type <Application,Lorg/junit/Test>")) {
                    System.out.println(callerMethod);
                    String className = (cgNode.getMethod().getDeclaringClass().getName().toString());
                    out.write(className + " " + callerMethod + "\n");
                }
            }
            out.close();
            System.out.println("————————输出method级测试用例选择结束————————");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
