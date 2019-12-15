package us.to.opti_grader.optigrader.Model;

import java.util.List;

import javax.xml.transform.Result;

public class MyResponse {
    public long multicat_id;
    public int success;
    public int failure;
    public int canonical_ids;
    public List<Result> results;
}
