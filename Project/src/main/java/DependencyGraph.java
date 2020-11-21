import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.cha.CHACallGraph;

import java.util.*;

/**
 * 这个类是自定义的用来记录项目依赖关系信息的图
 *
 * @author great_fish
 */
public abstract class DependencyGraph {
    // 记录生成dot图所需信息,即调用关系其中key为被调用者的类签名,value为调用者的类签名集合
    public HashMap<String, Set<String>> dependencymap = new HashMap<String, Set<String>>();

    /**
     * 记录所有的需要存储依赖关系的依赖图中的节点的name,
     * 如果是Class级依赖图存所有Application类名,
     * 方法级依赖图则存Application类所有自定义方法的方法签名
     */
    public Set<String> nodeName = new HashSet<String>();

    // 映射各方法标签到其对应cg图节点
    protected Map<String, CGNode> methodToNode = new HashMap<String, CGNode>();

    // 使用WALA框架得到的调用图
    protected CHACallGraph cg;

    /**
     * 生成项目依赖关系图,分析target目录下所有.class关系记录所需粒度的依赖关系
     *
     * @param targetPath target目录路径,根据路径可以获得target目录下所有.class文件
     */
    public abstract void init(String targetPath);

    /**
     * 将依赖图中信息转成dot文档(便于可视化
     *
     * @param path 输出Dot文档的路径
     */
    public void toDot(String path) {
        Utils.dotGenerate(dependencymap, path);
    }

    ;

    /**
     * 依赖关系图中存的是直接依赖，此方法可以获得节点的所有依赖的节点
     *
     * @param node 节点名
     * @return
     */
    public Set<String> getCallers(String node) {
        if (!dependencymap.containsKey(node))  //node没有任何直接依赖节点
            return null;
        Set<String> callers = dependencymap.get(node);
        Set<String> visited = new HashSet<String>(); //记录被访问过的节点，再次遇到时不压栈
        Stack<String> unVisit = new Stack<String>(); //记录尚未被访问的节点
        for (String caller : callers)
            unVisit.push(caller);
        while (!unVisit.isEmpty()) { //直到访问完所有可达节点
            String s = unVisit.peek();
            unVisit.pop();
            if (visited.contains(s))
                continue; //已经使用过s了就继续
            visited.add(s);
            if (dependencymap.containsKey(s))
                for (String tmp : dependencymap.get(s))
                    if (!visited.contains(tmp))
                        unVisit.push(tmp);
        }
        return visited;
    }

    /**
     * 返回方法名对应的节点
     *
     * @param method 方法名
     * @return 该方法名对应的CGNode节点
     */
    public CGNode getNodeOfMethod(String method) {
        return methodToNode.get(method);
    }
}
