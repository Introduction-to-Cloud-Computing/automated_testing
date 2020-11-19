/**
 * 进行测试用例选择的接口
 * @author great_fish
 */
public interface TestChoose {
    /**
     * 选择出受生产代码变更影响的测试用例,并进行相应的文件输出处理
     * @param changeInfoPath  变更信息所在文件路径
     * @param dependencyGraph 选择测试用例所需的依赖图
     */
    public void chooseTest(String changeInfoPath,DependencyGraph dependencyGraph);
}
