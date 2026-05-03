from __future__ import annotations

from dataclasses import dataclass
from typing import Dict, Optional, Tuple

import cv2
import mediapipe as mp
import numpy as np


@dataclass(frozen=True)
class PoseDetectorConfig:
    min_detection_confidence: float = 0.5
    min_tracking_confidence: float = 0.5


class PoseDetector:
    def __init__(self, cfg: PoseDetectorConfig = PoseDetectorConfig()) -> None:
        self._cfg = cfg
        self._mp_pose = mp.solutions.pose
        self._pose = self._mp_pose.Pose(
            static_image_mode=False,  # video tracking (required)
            model_complexity=1,
            enable_segmentation=False,
            smooth_landmarks=True,
            min_detection_confidence=cfg.min_detection_confidence,
            min_tracking_confidence=cfg.min_tracking_confidence,
        )

    def detect_landmarks(
        self, frame_bytes: bytes
    ) -> Tuple[bool, Optional[Dict[str, Tuple[float, float]]]]:
        """
        Returns (ok, landmarks_dict). Landmarks are normalized x,y in [0..1].
        """
        bgr = self._decode_frame(frame_bytes)
        if bgr is None:
            return False, None

        rgb = cv2.cvtColor(bgr, cv2.COLOR_BGR2RGB)
        res = self._pose.process(rgb)

        if not res.pose_landmarks:
            return True, {}

        lm = res.pose_landmarks.landmark
        return True, {
            "left_shoulder": (lm[self._mp_pose.PoseLandmark.LEFT_SHOULDER].x,
                              lm[self._mp_pose.PoseLandmark.LEFT_SHOULDER].y),
            "left_elbow": (lm[self._mp_pose.PoseLandmark.LEFT_ELBOW].x,
                           lm[self._mp_pose.PoseLandmark.LEFT_ELBOW].y),
            "left_wrist": (lm[self._mp_pose.PoseLandmark.LEFT_WRIST].x,
                           lm[self._mp_pose.PoseLandmark.LEFT_WRIST].y),
            "left_hip": (lm[self._mp_pose.PoseLandmark.LEFT_HIP].x,
                         lm[self._mp_pose.PoseLandmark.LEFT_HIP].y),
            "left_knee": (lm[self._mp_pose.PoseLandmark.LEFT_KNEE].x,
                          lm[self._mp_pose.PoseLandmark.LEFT_KNEE].y),
            "left_ankle": (lm[self._mp_pose.PoseLandmark.LEFT_ANKLE].x,
                           lm[self._mp_pose.PoseLandmark.LEFT_ANKLE].y),
            "right_shoulder": (lm[self._mp_pose.PoseLandmark.RIGHT_SHOULDER].x,
                               lm[self._mp_pose.PoseLandmark.RIGHT_SHOULDER].y),
            "right_elbow": (lm[self._mp_pose.PoseLandmark.RIGHT_ELBOW].x,
                            lm[self._mp_pose.PoseLandmark.RIGHT_ELBOW].y),
            "right_wrist": (lm[self._mp_pose.PoseLandmark.RIGHT_WRIST].x,
                            lm[self._mp_pose.PoseLandmark.RIGHT_WRIST].y),
            "right_hip": (lm[self._mp_pose.PoseLandmark.RIGHT_HIP].x,
                          lm[self._mp_pose.PoseLandmark.RIGHT_HIP].y),
            "right_knee": (lm[self._mp_pose.PoseLandmark.RIGHT_KNEE].x,
                           lm[self._mp_pose.PoseLandmark.RIGHT_KNEE].y),
            "right_ankle": (lm[self._mp_pose.PoseLandmark.RIGHT_ANKLE].x,
                            lm[self._mp_pose.PoseLandmark.RIGHT_ANKLE].y),
        }

    @staticmethod
    def _decode_frame(frame_bytes: bytes) -> Optional[np.ndarray]:
        arr = np.frombuffer(frame_bytes, dtype=np.uint8)
        img = cv2.imdecode(arr, cv2.IMREAD_COLOR)
        return img if img is not None else None
