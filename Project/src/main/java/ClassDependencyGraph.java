import com.ibm.wala.classLoader.ShrikeBTMethod;
import com.ibm.wala.ipa.callgraph.CGNode;

import java.util.*;

/**
 * 类级依赖图
 *
 * @author great_fish
 */
public class ClassDependencyGraph extends DependencyGraph {
    private Map<String, Set<String>> classToMethods = new HashMap<String, Set<String>>();//记录类的内部表示到该类所有方法的方法签名的映射，用于测试用例选择.

    @Override
    public void init(String targetPath) {
        System.out.println("开始加载" + targetPath + "处的项目啦");
        long startTime = System.currentTimeMillis();
        cg = Utils.InitCallGraph(targetPath, ClassDependencyGraph.class.getClassLoader());

        //根据调用图cg选择所需信息生成类级依赖图
        for (CGNode node : cg) {
            if (node.getMethod() instanceof ShrikeBTMethod) {
                // 一般地，本项目中所有和业务逻辑相关的方法都是ShrikeBTMethod对象
                ShrikeBTMethod method = (ShrikeBTMethod) node.getMethod();
                // 我们关心Allication类（非原生类）。使用Primordial类加载器加载的类都属于Java原生类，我们一般不关心。
                if ("Application".equals(method.getDeclaringClass().getClassLoader().toString())) {
                    Iterator<CGNode> iterator = cg.getPredNodes(node);//遍历调用该节点的前驱节点
                    while (iterator.hasNext()) {
                        CGNode next = iterator.next();
                        if ("Application".equals(next.getMethod().getDeclaringClass().getClassLoader().toString())) {//只需要属于Allication类的前驱节点
                            //增加当前节点对应类到前驱节点对应类的映射
                            Utils.mapAdd(dependencymap, method.getDeclaringClass().getName().toString(), next.getMethod().getDeclaringClass().getName().toString());
                        }
                    }
                    if (!node.getMethod().getSignature().endsWith("<init>()V")) //排除类的init方法
                        Utils.mapAdd(classToMethods, node.getMethod().getDeclaringClass().getName().toString(), node.getMethod().getSignature());
                    nodeName.add(node.getMethod().getDeclaringClass().getName().toString());
                    methodToNode.put(node.getMethod().getSignature(), node);
                }
            }
        }
        ;
        long endTime = System.currentTimeMillis();
        System.out.println("加载完成，用时" + (endTime - startTime) + "ms");
    }

    /**
     * 根据类名返回类中所有的方法名
     *
     * @param className 类名
     * @return 类中的所有方法签名
     */
    public Set<String> getMethosOfClass(String className) {
        return classToMethods.get(className);
    }
}
