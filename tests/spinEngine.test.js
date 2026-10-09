import { describe, it, expect } from 'vitest';
import { calculateWinningSegmentIndex, computeTargetRotation } from '../src/services/spinEngine.js';

describe('Spin Engine Mathematical Accuracy', () => {
  it('correctly calculates stop segment for all 8 segments', () => {
    const segmentCount = 8;
    const currentAngle = 0;

    for (let target = 0; target < segmentCount; target++) {
      const rotation = computeTargetRotation(currentAngle, target, segmentCount, 5);
      const resultIndex = calculateWinningSegmentIndex(currentAngle, rotation, segmentCount);
      expect(resultIndex).toBe(target);
    }
  });

  it('handles arbitrary segment sizes from 2 to 16', () => {
    for (let segments = 2; segments <= 16; segments++) {
      const target = Math.floor(segments / 2);
      const rotation = computeTargetRotation(45, target, segments, 4);
      const result = calculateWinningSegmentIndex(45, rotation, segments);
      expect(result).toBe(target);
    }
  });
});
