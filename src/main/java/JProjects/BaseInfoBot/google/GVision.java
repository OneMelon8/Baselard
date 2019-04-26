package JProjects.BaseInfoBot.google;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Feature.Type;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.cloud.vision.v1.ImageSource;
import com.google.cloud.vision.v1.TextAnnotation;
import com.google.protobuf.ByteString;

public class GVision {
	private static ImageAnnotatorClient client;

	public static void init() throws IOException {
		client = ImageAnnotatorClient.create();
	}

	public static void close() {
		System.out.println("Closing Vision API..");
		client.close();
		System.out.println("Vision API closed");
	}

	public static String ocr(String url) {
		List<AnnotateImageRequest> requests = new ArrayList<AnnotateImageRequest>();
		ImageSource imgSource = ImageSource.newBuilder().setImageUri(url).build();
		Image img = Image.newBuilder().setSource(imgSource).build();
		Feature feat = Feature.newBuilder().setType(Type.DOCUMENT_TEXT_DETECTION).build();
		AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
		requests.add(request);

		BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
		List<AnnotateImageResponse> responses = response.getResponsesList();

		StringBuilder sb = new StringBuilder();
		for (AnnotateImageResponse res : responses) {
			if (res.hasError())
				continue;
			TextAnnotation annotation = res.getFullTextAnnotation();
			sb.append(annotation.getText() + "\n\n");
		}
		return sb.toString().trim();
	}

	public static String ocr(File f) throws FileNotFoundException, IOException {
		List<AnnotateImageRequest> requests = new ArrayList<>();
		ByteString imgBytes = ByteString.readFrom(new FileInputStream(f));
		Image img = Image.newBuilder().setContent(imgBytes).build();

		Feature feat = Feature.newBuilder().setType(Type.DOCUMENT_TEXT_DETECTION).build();
		AnnotateImageRequest request = AnnotateImageRequest.newBuilder().addFeatures(feat).setImage(img).build();
		requests.add(request);

		BatchAnnotateImagesResponse response = client.batchAnnotateImages(requests);
		List<AnnotateImageResponse> responses = response.getResponsesList();

		StringBuilder sb = new StringBuilder();
		for (AnnotateImageResponse res : responses) {
			if (res.hasError())
				continue;
			TextAnnotation annotation = res.getFullTextAnnotation();
			sb.append(annotation.getText() + "\n\n");
		}
		sb.delete(sb.length() - 2, sb.length());
		return sb.toString();
	}
}
