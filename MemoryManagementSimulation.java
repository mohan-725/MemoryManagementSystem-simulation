import java.util.ArrayList;
import java.util.Collections;

class MemoryBlock {
    public int start;
    public int size;
    public boolean free;

    public MemoryBlock(int start, int size, boolean free) {
        this.start = start;
        this.size = size;
        this.free = free;
    }

    public String toString() {
        return "(" + start + ", " + (start + size - 1) + ", " + size + ", " + free + ")";
    }
}

class MemoryManager {
    public int memorySize;
    public ArrayList<MemoryBlock> allocationTable;

    public MemoryManager(int memorySize) {
        this.memorySize = memorySize;
        allocationTable = new ArrayList<MemoryBlock>();
        allocationTable.add(new MemoryBlock(0, memorySize, true));
    }

    public int allocate(int size, String algorithm) {
        int index = -1;
        int start = -1;
        int wastedMemory = -1;

        if (algorithm.equals("First-Fit")) {
            for (int i = 0; i < allocationTable.size(); i++) {
                MemoryBlock block = allocationTable.get(i);
                if (block.free && block.size >= size) {
                    index = i;
                    start = block.start;
                    wastedMemory = block.size - size;
                    break;
                }
            }
        } else if (algorithm.equals("Best-Fit")) {
            int minWastedMemory = Integer.MAX_VALUE;
            for (int i = 0; i < allocationTable.size(); i++) {
                MemoryBlock block = allocationTable.get(i);
                if (block.free && block.size >= size) {
                    int currentWastedMemory = block.size - size;
                    if (currentWastedMemory < minWastedMemory) {
                        index = i;
                        start = block.start;
                        wastedMemory = currentWastedMemory;
                        minWastedMemory = currentWastedMemory;
                    }
                }
            }
        } else if (algorithm.equals("Worst-Fit")) {
            int maxWastedMemory = -1;
            for (int i = 0; i < allocationTable.size(); i++) {
                MemoryBlock block = allocationTable.get(i);
                if (block.free && block.size >= size) {
                    int currentWastedMemory = block.size - size;
                    if (currentWastedMemory > maxWastedMemory) {
                        index = i;
                        start = block.start;
                        wastedMemory = currentWastedMemory;
                        maxWastedMemory = currentWastedMemory;
                    }
                }
            }
        }

        if (index == -1) {
            System.out.println("Memory allocation failed!");
            return -1;
        }

        MemoryBlock block = allocationTable.get(index);
        block.free = false;
        if (block.size == size) {
            return start;
        }

        MemoryBlock newBlock = new MemoryBlock(start + size, block.size - size, true);
        allocationTable.add(index + 1, newBlock);
        block.size = size;
        return start;
    }

    public void deallocate(int start) {
        for (int i = 0; i < allocationTable.size(); i++) {
            MemoryBlock block = allocationTable.get(i);
            if (block.start == start) {
                block.free = true;
                if (i > 0 && allocationTable.get(i - 1).free) {
                    MemoryBlock previousBlock = allocationTable.get(i - 1);
                    previousBlock.size += block.size;
                    allocationTable.remove(i);
                    i--;
                    block = previousBlock;
                }
                if (i < allocationTable.size() - 1 && allocationTable.get(i + 1).free) {
                    MemoryBlock nextBlock = allocationTable.get(i + 1);
                    block.size += nextBlock.size;
                    allocationTable.remove(i + 1);
                }
                return;
            }
        }
        System.out.println("Memory deallocation failed!");
    }

    public int getFragmentation() {
        int fragmentation = 0;
        for (int i = 0; i < allocationTable.size(); i++) {
            if (allocationTable.get(i).free) {
                fragmentation += allocationTable.get(i).size;
            }
        }
        return fragmentation;
    }

    public int getWastedMemory() {
        int wastedMemory = 0;
        for (int i = 0; i < allocationTable.size(); i++) {
            if (allocationTable.get(i).free) {
                wastedMemory += allocationTable.get(i).size;
            }
        }
        return wastedMemory;
    }

    public void printAllocationTable() {
        System.out.println("Allocation Table:");
        for (int i = 0; i < allocationTable.size(); i++) {
            System.out.println("Block " + i + ": " + allocationTable.get(i));
        }
    }
}

public class MemoryManagementSimulation {
    public static void main(String[] args) {
        MemoryManager memoryManager = new MemoryManager(1000);
        int start = memoryManager.allocate(200, "First-Fit");
        System.out.println("Allocation result: start = " + start);
        memoryManager.printAllocationTable();
        System.out.println("Fragmentation: " + memoryManager.getFragmentation());
        System.out.println("Wasted Memory: " + memoryManager.getWastedMemory());
        memoryManager.deallocate(start);
        memoryManager.printAllocationTable();

        start = memoryManager.allocate(400, "Best-Fit");
        System.out.println("Allocation result: start = " + start);
        memoryManager.printAllocationTable();
        System.out.println("Fragmentation: " + memoryManager.getFragmentation());
        System.out.println("Wasted Memory: " + memoryManager.getWastedMemory());
        memoryManager.deallocate(start);
        memoryManager.printAllocationTable();

        start = memoryManager.allocate(600, "Worst-Fit");
        System.out.println("Allocation result: start = " + start);
        memoryManager.printAllocationTable();
        System.out.println("Fragmentation: " + memoryManager.getFragmentation());
        System.out.println("Wasted Memory: " + memoryManager.getWastedMemory());
        memoryManager.deallocate(start);
        memoryManager.printAllocationTable();
    }
}