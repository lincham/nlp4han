package com.lc.nlp4han.constituent.pcfg;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import com.lc.nlp4han.constituent.ConstituentMeasure;
import com.lc.nlp4han.constituent.ConstituentTree;
import com.lc.nlp4han.constituent.PlainTextByTreeStream;
import com.lc.nlp4han.ml.util.FileInputStreamFactory;
import com.lc.nlp4han.ml.util.ObjectStream;

public class CKYEvalTool
{
	public static void main(String[] args) throws IOException
	{
		if (args.length < 1)
		{
			return;
		}
		
		String trainFile = null;
		String goldFile = null;
		String encoding = null;
		for (int i = 0; i < args.length; i++)
		{
			if (args[i].equals("-model"))
			{
				trainFile = args[i + 1];
				i++;
			}
			else if (args[i].equals("-gold"))
			{
				goldFile = args[i + 1];
				i++;
			}
			else if (args[i].equals("-encoding"))
			{
				encoding = args[i + 1];
				i++;
			}
		}
		eval(trainFile, goldFile, encoding);
	}

	public static void eval(String trainFile, String goldFile, String encoding) throws IOException
	{
		PCFG p2nf = new PCFG(new FileInputStream(new File(trainFile)), encoding);
		CKYParserEvaluator evaluator = new CKYParserEvaluator(p2nf);
		
		ConstituentMeasure measure = new ConstituentMeasure();
		evaluator.setMeasure(measure);
		
		ObjectStream<String> treeStream = new PlainTextByTreeStream(new FileInputStreamFactory(new File(goldFile)),
				encoding);
		ObjectStream<ConstituentTree> sampleStream = new ConstituentTreeStream(treeStream);
		evaluator.evaluate(sampleStream);
		
		ConstituentMeasure measureRes = evaluator.getMeasure();
		System.out.println(measureRes);
	}

}
