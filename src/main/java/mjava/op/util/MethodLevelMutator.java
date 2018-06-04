package mjava.op.util;


import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import edu.ecnu.sqslab.mjava.MutationSystem;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * Created by user on 2018/5/5.
 * @author Jian Liu
 */
public class MethodLevelMutator extends Mutator {
    protected  String currentMethodSignature = null;
    Node parentNode = null;

    public MethodLevelMutator(CompilationUnit compileUnit){
        super(compileUnit);
    }

    /**
     * Retrieve the source's file name
     */
    public String getSourceName(String op_name) {
        // make directory for the mutant
        String dir_name = MutationSystem.MUTANT_PATH + "/" + currentMethodSignature + "/" + op_name + "_" + this.num;
        File f = new File(dir_name);
        f.mkdir();

        // return file name
        String name;
        name = dir_name + "/" +  MutationSystem.CLASS_NAME + ".java";
        return name;
    }

    /**
     * Return an ID of a given operator name
     * @param op_name
     * @return
     */
    public String getMuantID(String op_name)
    {
        String str = op_name + "_" + this.num;
        return str;
    }

    public PrintWriter getPrintWriter(String f_name) throws IOException
    {
        File outfile = new File(f_name);
        FileWriter fout = new FileWriter( outfile );
        PrintWriter out = new PrintWriter( fout );
        return out;
    }


    public void visit(MethodDeclaration p, Object obj)
    {
        if(MutationSystem.ThreadFile){
            if(isThreadMethodDeclaration(p)){
                //System.out.println("========"+p.getNameAsString());
                currentMethodSignature = getMethodSignature(p);
            }
            else {
                currentMethodSignature = null;
            }
        }
        else {
            currentMethodSignature = getMethodSignature(p);
        }
        super.visit(p,obj);
    }

    public void visit(ConstructorDeclaration p, Object obj)
    {
        if(MutationSystem.ThreadFile){
            currentMethodSignature = null;
        }
        else {
            currentMethodSignature = getConstructorSignature(p);
        }
        super.visit(p,obj);
    }

}
