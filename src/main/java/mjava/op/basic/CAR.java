package mjava.op.basic;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.resolution.types.ResolvedType;
import mjava.op.util.MethodLevelMutator;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * 常量替代数组引用; int[] a; method(a); method(a[0])/method(1);
 */
 /**
 * Created by user on 2018/5/7.
 * @author Jian Liu
 */
public class CAR extends MethodLevelMutator {

    public CAR(CompilationUnit comp_unit) {
        super(comp_unit);
    }

    public void visit(MethodCallExpr m, Object obj){
        super.visit(m,obj);
        NodeList<Expression> expList = m.getArguments();
        for (int i = 0; i < expList.size(); i++) {
            Expression e = expList.get(i);
            try{
                //ResolvedType t = MutantsGenerator.getJavaParserFacade().getType(e);
                ResolvedType t = e.calculateResolvedType();
                if (t.isArray()) {
                    MutantGen(m, e, i);
                }
            }
           catch (Exception uex){
               // System.err.println(e.toString()+" ,Unsolved Symbol Exception, ignore...");
           }
        }
    }

    private void MutantGen(MethodCallExpr m, Expression exp, int pos) {
        MethodCallExpr mutant;
        mutant = m.clone();
        ArrayAccessExpr aa = new ArrayAccessExpr();
        aa.setName(exp);
        aa.setIndex(new IntegerLiteralExpr(0));
        // System.out.println("-----"+aa.toString()+"-------");
        mutant.getArguments().set(pos, aa);
        outputToFile(m, mutant);
    }

    /**
     * Output CAR mutants to files
     *
     * @param original
     * @param mutant
     */
    public void outputToFile(MethodCallExpr original, MethodCallExpr mutant) {
        if (comp_unit == null || currentMethodSignature == null){
            return;
        }
        num++;
        String f_name = getSourceName("CAR");
        String mutant_dir = getMuantID("CAR");
        try {
            PrintWriter out = getPrintWriter(f_name);
            CAR_Writer writer = new CAR_Writer(mutant_dir, out);
            writer.setMutant(original, mutant);
            writer.setMethodSignature(currentMethodSignature);
            writer.writeFile(comp_unit);
            out.flush();
            out.close();
        }
        catch (IOException e) {
            System.err.println("CAR: Fails to create " + f_name);
        }
    }
}
