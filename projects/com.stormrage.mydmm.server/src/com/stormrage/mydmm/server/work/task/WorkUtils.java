package com.stormrage.mydmm.server.work.task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.stormrage.mydmm.server.task.TaskErrorCode;
import com.stormrage.mydmm.server.task.TaskException;
import com.stormrage.mydmm.server.task.TaskUtils;
import com.stormrage.mydmm.server.utils.Guid;
import com.stormrage.mydmm.server.utils.StringUtils;
import com.stormrage.mydmm.server.work.WorkActressType;
import com.stormrage.mydmm.server.work.WorkBean;
import com.stormrage.mydmm.server.work.WorkPictureBean;
import com.stormrage.mydmm.server.work.WorkPictureType;


/**
 * 作品相关工具类
 * @author StormrageWang
 * @date 2014年5月18日
 */
public class WorkUtils {
	
	private static final String EMPTY = "----";
	private static final String TIME_LENGTH_SUFFIX = "分";
	private static final String DATE_FORMAT = "yyyy/MM/dd";
	private static final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
	private static final String TITLE_CODE = "品番：";
	private static final String TITLE_TIME_LENGTH = "収録時間：";
	private static final String TITLE_ACTRESS = "出演者：";
	private static final String TITLE_DATE_ANIMATION = "配信開始日：";
	private static final String TITLE_DATE_SINGLE_RENT = "貸出開始日：";
	private static final String TITLE_DATE_MAIL_ORDER = "発売日：";
	
	/**
	 * 基本信息包括完整番号，简单番号，日期，时长，演员类型，这些信息必须有<br/>
	 * 不包括标题，地址，中文名称
	 * @param workBean
	 * @param doc
	 * @throws TaskException
	 */
	public static void fullBaseInfo(WorkBean workBean, Document doc) throws TaskException {
		Elements infoTables = doc.select("table");
		if(infoTables.size() < 3){
			throw new TaskException("页面中未包含作品信息", TaskErrorCode.TASK_ANALYTICS_GET);
		}
		Element infoTable = doc.select("table").get(2);
		Elements infoTrs = infoTable.select("tr");
		//看每一行有没有需要的信息
		for(Element infoTr : infoTrs){
			Elements tds = infoTr.select("td");
			if(tds.size() != 2){
				continue;
			}
			fullInfoByTds(workBean, tds);
		}
		//检查信息是否都取到
		checkBaseInfo(workBean);
	}
	
	
	private static void fullInfoByTds(WorkBean workBean, Elements tds) throws TaskException {
		Element titleTd =  tds.get(0);
		Element infoTd = tds.get(1);
		String title = titleTd.html();
		String info = infoTd.html();
		if(TITLE_CODE.equals(title)){//番号
			workBean.setCode(info);
			workBean.setSimpleCode(getSimpleCode(info));
			return;
		}
		if(TITLE_TIME_LENGTH.equals(title)){//时长
			workBean.setTimeLength(getTimeLength(info));
			return;
		}
		if(TITLE_ACTRESS.endsWith(title)){//演员类型
			Elements actressLinks = infoTd.select("a");
			int actressCount = actressLinks.size();
			workBean.setActressType(getActressType(actressCount));
			return;
		}
		if(TITLE_DATE_ANIMATION.equals(title)//发表时期
				||TITLE_DATE_MAIL_ORDER.equals(title)
				||TITLE_DATE_SINGLE_RENT.equals(title)){
			workBean.setDate(getDate(info));
		}
	}
	
	private static void checkBaseInfo(WorkBean workBean) throws TaskException {
		if(StringUtils.isEmpty(workBean.getCode())){
			throw new TaskException("未获取到作品的番号信息", TaskErrorCode.TASK_ANALYTICS_GET);
		}
		if(StringUtils.isEmpty(workBean.getSimpleCode())){
			throw new TaskException("未获取到作品的简单番号信息", TaskErrorCode.TASK_ANALYTICS_GET);
		}
		if(workBean.getTimeLength() == 0){
			throw new TaskException("未获取到作品的时长信息", TaskErrorCode.TASK_ANALYTICS_GET);
		}
		if(workBean.getDate() == null){
			throw new TaskException("未获取到作品的日期信息", TaskErrorCode.TASK_ANALYTICS_GET);
		}
		if(workBean.getActressType() == null){
			throw new TaskException("未获取到作品的演员类型信息", TaskErrorCode.TASK_ANALYTICS_GET);
		}
		
	}
	
	/**
	 * 封面小图，一定有
	 * @param doc
	 * @return
	 */
	public static WorkPictureBean getSimpleCover(Document doc) {
		Element coverDiv = doc.select("#sample-video").first();
		Element simpleCoverImage = coverDiv.select("img").first();
		String simpleCoverUrl = simpleCoverImage.attr("src");
		WorkPictureBean simpleCoverBean = new WorkPictureBean();
		simpleCoverBean.setGuid(Guid.newGuid());
		simpleCoverBean.setUrl(simpleCoverUrl);
		simpleCoverBean.setType(WorkPictureType.COVER_ORDINARY);
		return simpleCoverBean;
	}
	
	/**
	 * 封面大图，可以没有，没有时返回null
	 * @param doc
	 * @return
	 */
	public static WorkPictureBean getFullCover(Document doc) {
		Element coverDiv = doc.select("#sample-video").first();
		Element fullCoverLink =  coverDiv.select("a").first();
		if(fullCoverLink == null){
			return null;
		}
		String fullCoverUrl = fullCoverLink.attr("href");
		WorkPictureBean fullCoverBean = new WorkPictureBean();
		fullCoverBean.setGuid(Guid.newGuid());
		fullCoverBean.setUrl(fullCoverUrl);
		fullCoverBean.setType(WorkPictureType.COVER_HD);
		return fullCoverBean;
	}
	
	
	/**
	 * 预览图，可以没有，没有时返回空图片数组
	 * @param doc
	 * @return
	 */
	public static WorkPictureBean[] getPreviewPictures(Document doc){
		Element previewDiv = doc.select("#sample-image-block").first();
		if(previewDiv == null) {
			return new WorkPictureBean[0];
		}
		Elements pictureLinks = previewDiv.select("a");
		WorkPictureBean[] pictureBeans = new WorkPictureBean[pictureLinks.size() * 2];
		int i = 0;
		for(Element pictureLink : pictureLinks){
			//小图
			String smallUrl = pictureLink.children().first().attr("src");
			WorkPictureBean smallBean = new WorkPictureBean();
			smallBean.setGuid(Guid.newGuid());
			smallBean.setUrl(smallUrl);
			smallBean.setType(WorkPictureType.PREVIEW_ORDINARY);
			pictureBeans[i] = smallBean;
			i++;
			//对应的大图
			WorkPictureBean bigBean = new WorkPictureBean();
			String bugUrl = WorkUtils.getFullPictureUrl(smallUrl);
			bigBean.setGuid(Guid.newGuid());
			bigBean.setUrl(bugUrl);
			bigBean.setType(WorkPictureType.PREVIEW_HD);
			pictureBeans[i] = bigBean;
			i++;
		}
		return pictureBeans;
	}
	
	public static WorkActressType getActressType(int actressCount) throws TaskException {
		if(actressCount == 1){
			return WorkActressType.SINGLE;
		} else if(actressCount > 1 && actressCount < 5){
			return WorkActressType.SERVERAL;
		} else if(actressCount > 4){
			return WorkActressType.COLLECTION;
		}
		throw new TaskException("无法获取作品的演员类型，其演员个数为【" + actressCount + "】", TaskErrorCode.TASK_ANALYTICS_GET);
	}
	
	public static Date getDate(String dateStr) throws TaskException {
		try {
			return sdf.parse(dateStr);
		} catch (ParseException e) {
			throw new TaskException("无法获取作品的时期，其日期字符串【" + dateStr + "】无法转为时期", TaskErrorCode.TASK_ANALYTICS_GET);
		}
	}
	
	public static int getTimeLength(String fullStr) throws TaskException {
		if(EMPTY.endsWith(fullStr)){
			return -1;
		}
		int index = fullStr.indexOf(TIME_LENGTH_SUFFIX);
		if(index < 0){
			System.out.println(fullStr);
		}
		try{
			String timeLengthStr = fullStr.substring(0, index);
			try{
				return Integer.valueOf(timeLengthStr);
			} catch (NumberFormatException e){
				throw new TaskException("时长【" + timeLengthStr + "】无法转为数字", TaskErrorCode.TASK_ANALYTICS_GET);
			} 
		} catch (IndexOutOfBoundsException e){
			throw new TaskException("时长【" + fullStr + "】无法确定数字的未知", TaskErrorCode.TASK_ANALYTICS_GET);
		}
	}

	/**
	 * 代码转成简单代码
	 * @param fullCode
	 * @return
	 */
	public static String getSimpleCode(String fullCode) throws TaskException {
		//从后往前分析
		int startIndex = fullCode.length() - 1;
		//如果不是数字字符结尾，先去掉结尾的字符
		char lastChar = fullCode.charAt(startIndex);
		if(!isNumber(lastChar)){
			for( ; startIndex >= 0; startIndex--){
				char c = fullCode.charAt(startIndex);
				if(isNumber(c)){
					break;
				} 
			}
		}
		char[] codeChars = new char[fullCode.length()];
		char[] signChars = new char[fullCode.length()];
		int codeIndex = 0, signIndex = 0;
		boolean codeCompleted = false, signCompleted = false;
		for( ; startIndex >= 0; startIndex--){
			char c = fullCode.charAt(startIndex);
			if(codeCompleted && signCompleted){//2个都已解析出来
				break;
			} else if(codeCompleted & !signCompleted){//只解析出来代码
				if(isLowerLetter(c)){
					signChars[signIndex] = c;
					signIndex ++;
				} else {
					signCompleted = true;
				}
			} else if(!codeCompleted){//代码还未解析出来
				if(isNumber(c)){
					codeChars[codeIndex] = c;
					codeIndex ++;
				} else {
					codeCompleted = true;
					signChars[signIndex] = c;
					signIndex ++;
				}
			}
		}
		String codeStr = new String(codeChars, 0, codeIndex);
		String signStr = new String(signChars, 0, signIndex);
		codeStr = StringUtils.reverse(codeStr);
		signStr = StringUtils.reverse(signStr);
		if(StringUtils.isEmpty(codeStr)
				||StringUtils.isEmpty(signStr)){
			throw new TaskException("无法获取【" + fullCode + "】的番号", TaskErrorCode.TASK_ANALYTICS_GET);
		}
		int code = Integer.parseInt(codeStr);
		codeStr = String.format("%03d", code);
		return signStr + "-" + codeStr;
	}
	
	private static boolean isLowerLetter(char c){
		if(isNumber(c)){
			return false;
		}
		return true;
	}
	
	private static boolean isNumber(char c){
		if(c < '0' || c > '9'){
			return false;
		}
		return true;
	}
	
	public static String getFullPictureUrl(String pictureUrl){
		int index = pictureUrl.lastIndexOf("-");
		String prefix = pictureUrl.substring(0, index);
		String suffix = pictureUrl.substring(index);
		return prefix + "jp" + suffix;
	}
	
	public static void main(String[] args) {
		try {
			WorkBean workBean = new WorkBean();
			//使用代理设置
			TaskUtils.useProxy("127.0.0.1", "8087");
			//测试【動画】
			Document doc1 = TaskUtils.getDocument("http://www.unblockdmm.com/browse.php?u=http%3A%2F%2Fwww.dmm.co.jp%2Fdigital%2Fvideoa%2F-%2Fdetail%2F%3D%2Fcid%3Dmide00007&b=0");
			fullBaseInfo(workBean, doc1);
			System.out.println(workBean.getDescription());
			//测试【月額動画】
			Document doc2 = TaskUtils.getDocument("http://www.unblockdmm.com/browse.php?u=http%3A%2F%2Fwww.dmm.co.jp%2Fmonthly%2Fmoodyz%2F-%2Fdetail%2F%3D%2Fcid%3Dmide00007%2F&b=0");
			fullBaseInfo(workBean, doc2);
			System.out.println(workBean.getDescription());
			//测试【1円動画】
			Document doc3 = TaskUtils.getDocument("http://www.unblockdmm.com/browse.php?u=http%3A%2F%2Fwww.dmm.co.jp%2Fppm%2Fvideo%2F-%2Fdetail%2F%3D%2Fcid%3Dmide00007&b=0");
			fullBaseInfo(workBean, doc3);
			System.out.println(workBean.getDescription());
			//测试【通販】
			Document doc4 = TaskUtils.getDocument("http://www.unblockdmm.com/browse.php?u=http%3A%2F%2Fwww.dmm.co.jp%2Fmono%2Fdvd%2F-%2Fdetail%2F%3D%2Fcid%3Dmide007&b=0");
			fullBaseInfo(workBean, doc4);
			System.out.println(workBean.getDescription());
			//测试【単品レンタル】
			Document doc5 = TaskUtils.getDocument("http://www.unblockdmm.com/browse.php?u=http%3A%2F%2Fwww.dmm.co.jp%2Frental%2Fppr%2F-%2Fdetail%2F%3D%2Fcid%3D4mide007&b=0");
			fullBaseInfo(workBean, doc5);
			System.out.println(workBean.getDescription());
			//测试【月額レンタル】
			Document doc6 = TaskUtils.getDocument("http://www.unblockdmm.com/browse.php?u=http%3A%2F%2Fwww.dmm.co.jp%2Frental%2F-%2Fdetail%2F%3D%2Fcid%3D4mide007&b=0");
			fullBaseInfo(workBean, doc6);
			System.out.println(workBean.getDescription());
		} catch (TaskException e) {
			e.printStackTrace();
		}
	}
}
