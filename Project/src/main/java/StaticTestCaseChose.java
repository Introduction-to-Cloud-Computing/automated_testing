/**
 * 整个工具的入口,负责全局的调用以完成测试用例选择功能
 *
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
     *
     * @param args 命令行输入的参数
     */
    public static void main(String[] args) {
        char granularity = args[0].charAt(1);
        String project_target = args[1];
        String change_info = args[2];
//        char granularity='m';
//        String project_target="D:\\code\\automatedtesting2020\\Data\\ClassicAutomatedTesting\\5-MoreTriangle\\target";
//        String change_info="D:\\code\\automatedtesting2020\\Data\\ClassicAutomatedTesting\\5-MoreTriangle\\data\\change_info.txt";
        try {
            checkArgsValid(project_target, change_info, args.length);
            //事先检查路径是否都存在
            initFactory(granularity);
            dependencyGraph.init(project_target);
            dependencyGraph.toDot("tmp.dot");
            testChoose.chooseTest(change_info, dependencyGraph);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 根据粒度参数来初始化依赖图和测试用例选择接口
     *
     * @param granularity 粒度参数 m代表方法级,c代表类级
     */
    private static void initFactory(char granularity) throws Exception {
        if (granularity == 'c') {//如果是类级粒度
            dependencyGraph = new ClassDependencyGraph();
            testChoose = new ClassTestChoose();
        } else if (granularity == 'm') {//如果是方法级粒度
            dependencyGraph = new MethodDependencyGraph();
            testChoose = new MethodTestChoose();
        } else {
            throw new Exception("仅支持m/c参数级别的粒度选择");
        }
    }

    /**
     * 检查参数是否有效
     *
     * @param project_target 项目路径
     * @param change_info    变更信息路径
     * @param length         参数个数
     * @throws Exception
     */
    private static void checkArgsValid(String project_target, String change_info, int length) throws Exception {
        if (!Utils.checkFileExit(project_target)) {
            throw new Exception("target路径不存在，请检查");
        }
        if (!Utils.checkFileExit(change_info)) {
            throw new Exception("change_info路径不存在，请检查");
        }
        if (length != 3)
            throw new Exception("请按照-c/m target_path change_info_path格式输入参数");
    }
}
