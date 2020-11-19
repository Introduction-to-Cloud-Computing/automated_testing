import com.ibm.wala.classLoader.ShrikeBTMethod;
import com.ibm.wala.ipa.callgraph.CGNode;


import java.util.Iterator;


/**
 * 方法级依赖图
 * @author great_fish
 */
public class MethodDependencyGraph extends DependencyGraph {
    @Override
    public void init(String targetPath) {
        System.out.println("开始加载" + targetPath + "处的项目啦");
        long startTime = System.currentTimeMillis();
        cg = Utils.InitCallGraph(targetPath, MethodDependencyGraph.class.getClassLoader());

        //遍历cg中所有的节点，根据需要生成所需要的调用依赖图
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
                            //增加当前方法到前驱方法的映射
                            Utils.mapAdd(dependencymap, node.getMethod().getSignature(), next.getMethod().getSignature());
                        }
                    }
                    methodToNode.put(node.getMethod().getSignature(), node);
                    nodeName.add(node.getMethod().getSignature());
                    //    System.out.println(node.getMethod().getDeclaringClass().getName().toString()+"        "+node.getMethod().getSignature());
                }
            }
        }

        long endTime = System.currentTimeMillis();
        System.out.println("加载完成，用时" + (endTime - startTime) + "ms");
    }

}
