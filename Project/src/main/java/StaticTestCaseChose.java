/**
 * 整个工具的入口,负责全局的调用以完成测试用例选择功能
 * @author great_fish
 */
public class StaticTestCaseChose {

    /**
     * 自定义的依赖图
     */
    static DependencyGraph dependencyGraph;
    /**
     * 提供测试用例选择的接口
     */
    static TestChoose testChoose;

    /**
     * 项目入口,根据命令行输入信息和项目进行对应粒度的测试用例选择
     * 采用面向接口编程思想以及抽象工厂的设计模式
     * 设计了测试选择的接口以及依赖图抽象类
     */
    public static void main(String[] args) {
//        for (String arg : args)
//            System.out.println(arg);
        char granularity=args[0].charAt(1);
        String project_target=args[1];
        String change_info=args[2];
//        char granularity='m';
//        String project_target="D:\\code\\automatedtesting2020\\Data\\ClassicAutomatedTesting\\5-MoreTriangle\\target";
//        String change_info="D:\\code\\automatedtesting2020\\Data\\ClassicAutomatedTesting\\5-MoreTriangle\\data\\change_info.txt";
        try {
            initFactory(granularity);
            dependencyGraph.init(project_target);
            dependencyGraph.toDot("tmp.dot");
            testChoose.chooseTest(change_info, dependencyGraph);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 根据粒度参数来初始化依赖图和测试用例选择接口
     * @param granularity 粒度参数 m代表方法级,c代表类级
     */
    private static void initFactory(char granularity) throws Exception {
        if (granularity == 'c') {//如果是类级粒度
            dependencyGraph = new ClassDependencyGraph();
            testChoose = new ClassTestChoose();
        } else if(granularity=='m'){//如果是方法级粒度
            dependencyGraph = new MethodDependencyGraph();
            testChoose = new MethodTestChoose();
        }
        else{
            throw new Exception("仅支持m/c参数级别的粒度选择");
        }
    }
}
