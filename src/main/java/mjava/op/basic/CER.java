package mjava.op.basic;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.ConditionalExpr;
import mjava.op.util.MethodLevelMutator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * 条件表达式的真和假的结果值变异为一样的；a?b:c --> a?b:b 或 a?c:c
 */
 /**
 * Created by user on 2018/5/7.
  * @author Jian Liu
 */
public class CER extends MethodLevelMutator {
    private  static final Logger logger = LoggerFactory.getLogger(CER.class);

    public CER(CompilationUnit comp_unit) {
        super(comp_unit);
    }

    public void visit(ConditionalExpr cex,Object obj){
        true2False(cex);
        false2true(cex);
        super.visit(cex,obj);
    }
     private void true2False(ConditionalExpr meth) {
         ConditionalExpr mutant;
         mutant =  meth.clone();
         mutant.setElseExpr(meth.getThenExpr());
         outputToFile(meth, mutant);
     }

     private void false2true(ConditionalExpr meth) {
         ConditionalExpr mutant;
         mutant = meth.clone();
         mutant.setThenExpr(meth.getElseExpr());
         outputToFile(meth, mutant);
     }
    /**
     * Output CER mutants to files
     *
     * @param original
     * @param mutant
     */
    public void outputToFile(ConditionalExpr original, ConditionalExpr mutant) {
        if (comp_unit == null || currentMethodSignature == null){
            return;
        }
        num++;
        String f_name = getSourceName("CER");
        String mutant_dir = getMuantID("CER");
        try {
            PrintWriter out = getPrintWriter(f_name);
            CER_Writer writer = new CER_Writer(mutant_dir, out);
            writer.setMutant(original, mutant);
            writer.setMethodSignature(currentMethodSignature);
            writer.writeFile(comp_unit);
            out.flush();
            out.close();
        }
        catch (IOException e) {
            System.err.println("CER: Fails to create " + f_name);
            logger.error("Fails to create " + f_name);
        }
    }
}
