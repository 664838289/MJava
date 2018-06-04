package mjava.op.basic;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.ForStmt;
import mjava.op.util.MethodLevelMutator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.NoSuchElementException;

/**
 * for循环变异； for(int i=0;i<size;i++) --> for(int i=0;i<size;i=size)/for(int i=0;i<size-1;i++)
 */
 /**
 * Created by user on 2018/5/7.
  * @author Jian Liu
 */
public class FLR extends MethodLevelMutator {
    private  static final Logger logger = LoggerFactory.getLogger(FLR.class);

     public FLR(CompilationUnit comp_unit) {
         super(comp_unit);
     }

     public void visit(ForStmt fs,Object obj){
         super.visit(fs,obj);
         try{
             if(fs.getCompare().get().isBinaryExpr()){
                 //System.out.println(fs.toString());
                 minusMutantGen(fs);
                 assignmentMutantGen(fs);
             }
         }
         catch (NoSuchElementException e){
             System.err.println("FLR: No value present!!!");
         }
     }

     private void minusMutantGen(ForStmt f) {
         try{
             ForStmt fs =  f.clone();
             BinaryExpr be =(BinaryExpr)fs.getCompare().get();
             if (be == null) {
                 return;
             }
             BinaryExpr beTemp = new BinaryExpr(be.getRight(), new IntegerLiteralExpr("1"),BinaryExpr.Operator.MINUS);
             be.setRight(beTemp);
             outputToFile(f, fs);
         }
         catch (NoSuchElementException e){
             // no compare operator
             return;
         }
     }

     private void assignmentMutantGen(ForStmt f) {
         try{
             ForStmt fs2 = f.clone();
             BinaryExpr be2 = (BinaryExpr) fs2.getCompare().get();
             if (be2 == null) {
                 return;
             }
             AssignExpr ae = new AssignExpr(be2.getLeft(), be2.getRight(),AssignExpr.Operator.ASSIGN);
             fs2.setUpdate(new NodeList(ae));
             outputToFile(f, fs2);
         }
         catch (NoSuchElementException e){
             // no compare operator
             return;
         }
     }

     /**
      * Output FLR mutants to files
      *
      * @param original
      * @param mutant
      */
     public void outputToFile(ForStmt original, ForStmt mutant) {
         if (comp_unit == null || currentMethodSignature == null){
             return;
         }
         num++;
         String f_name = getSourceName("FLR");
         String mutant_dir = getMuantID("FLR");
         try {
             PrintWriter out = getPrintWriter(f_name);
             FLR_Writer writer = new FLR_Writer(mutant_dir, out);
             writer.setMutant(original, mutant);
             writer.setMethodSignature(currentMethodSignature);
             writer.writeFile(comp_unit);
             out.flush();
             out.close();
         }
         catch (IOException e) {
             System.err.println("FLR: Fails to create " + f_name);
             logger.error("Fails to create " + f_name);
         }
     }
}
