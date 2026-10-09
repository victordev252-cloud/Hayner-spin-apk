// Spin Engine & Physics Math

export function calculateWinningSegmentIndex(currentAngle, totalRotation, segmentCount) {
  const arcAngle = 360 / segmentCount;
  const finalAngle = (currentAngle + totalRotation) % 360;
  // Pointer is at top (270 degrees)
  const unrotatedPointer = ((270 - finalAngle) % 360 + 360) % 360;
  return Math.floor(unrotatedPointer / arcAngle) % segmentCount;
}

export function computeTargetRotation(currentAngle, targetIndex, segmentCount, fullRotations = 6) {
  const arcAngle = 360 / segmentCount;
  const segmentCenter = (targetIndex + 0.5) * arcAngle;
  const targetRemainder = ((270 - segmentCenter) % 360 + 360) % 360;
  const currentRem = ((currentAngle % 360) + 360) % 360;
  let delta = targetRemainder - currentRem;
  if (delta < 0) delta += 360;
  return (fullRotations * 360) + delta;
}
