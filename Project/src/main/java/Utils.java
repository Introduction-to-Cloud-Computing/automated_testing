import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.cha.CHACallGraph;
import com.ibm.wala.ipa.callgraph.impl.AllApplicationEntrypoints;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.util.config.AnalysisScopeReader;
import com.ibm.wala.util.io.FileProvider;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 项目中用到的一些工具
 *
 * @author great_fish
 */
public class Utils {

    /**
     * 根据项目初始化调用图
     *
     * @param targetPath  target目录路径，目录下.class文件是所需分析对象
     * @param classLoader 类加载器
     * @return target项目的调用图
     */
    public static CHACallGraph InitCallGraph(String targetPath, ClassLoader classLoader) {
        try {
            AnalysisScope scope = AnalysisScopeReader.readJavaScope("scope.txt",
                    new FileProvider().getFile("exclusion.txt"),
                    classLoader);
            ArrayList<File> fileList = Utils.getFile(targetPath, new ArrayList<File>());//得到所有的.class文件
            for (File file : fileList)
                scope.addClassFileToScope(ClassLoaderReference.Application, file);//将.class文件逐个添加进分析域中
            ClassHierarchy cha = ClassHierarchyFactory.makeWithRoot(scope);//生成类层次关系对象
            Iterable<Entrypoint> eps = new AllApplicationEntrypoints(scope, cha);//针对所有Application类（非原生类）生成进入点

            //利用CHA算法构建调用图
            CHACallGraph cg = new CHACallGraph(cha);
            cg.init(eps);
            return cg;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获得指定路径下所有.class文件
     *
     * @param path     target文件夹路径
     * @param fileList .class文件数组
     * @return 返回目录下所有./class文件
     */
    public static ArrayList<File> getFile(String path, ArrayList<File> fileList) {
        File file = new File(path);
        File[] array = file.listFiles(); // 获得该文件夹内的所有文件
        for (int i = 0; i < array.length; ++i) {
            if (array[i].isFile())// 如果是文件
            {
                if (array[i].getName().endsWith(".class"))//.class文件
                    fileList.add(array[i]);
            } else if (array[i].isDirectory())// 如果是文件夹
            {
                getFile(array[i].getAbsolutePath(), fileList);//遍历该文件夹下子项
            }
        }
        return fileList;
    }

    /**
     * 根据key存不存在分情况处理map
     *
     * @param map         需处理的map
     * @param key         需添加的key
     * @param appendValue 需添加的value
     */
    public static void mapAdd(Map<String, Set<String>> map, String key, String appendValue) {
        // System.out.println(key+"                 "+appendValue);
        if (!map.containsKey(key)) {//map中没有该key时，新增一个(key,value)对
            Set<String> tmp = new HashSet<String>();
            tmp.add(appendValue);
            map.put(key, tmp);
        } else {
            map.get(key).add(appendValue);
        }
    }

    /**
     * 生成dot文件到对应Path
     *
     * @param map  一个记录依赖关系的map
     * @param path dot文件指定输出路径
     */
    public static void dotGenerate(Map<String, Set<String>> map, String path) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(path));
            out.write("digraph cmd_class {\n");
            System.out.println("————————开始生成dot啦—————————");
            for (Map.Entry<String, Set<String>> entry : map.entrySet()) {//遍历map每一项
                Set<String> tmp = entry.getValue();
                for (String s : tmp) {
                    out.write("\"" + entry.getKey() + "\" -> \"" + s + "\"\n");
                }
            }
            out.write("}");
            out.close();
            System.out.println("————————生成完dot啦————————");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 按行读取指定文件
     *
     * @param filePath 文件路径名
     * @return 返回存储文件每一行的ArrayList
     */
    public static ArrayList<String> readFile(String filePath) {
        ArrayList<String> lines = new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String line = null;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    /**
     * 检查给定路径名表示的文件或目录是否存在
     *
     * @param filePath 路径名
     * @return 路径下是否存在文件/目录
     */
    public static boolean checkFileExit(String filePath) {
        File file = new File(filePath);
        if (!file.exists())
            return false;
        return true;
    }
}
